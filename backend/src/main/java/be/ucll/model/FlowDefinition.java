package be.ucll.model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import be.ucll.exception.DomainException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

@Document(collection = "FlowDefinition")
public class FlowDefinition {

    @Id
    private String id;
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotEmpty
    private List<Process> processes;
    private Set<String> triggerableBy = new HashSet<>();
    private boolean anyTrigger = false;

    private LocalDateTime updatedAt;

    public FlowDefinition(String title, String description, Set<String> triggerableBy, List<? extends Process> processes) {
        setTitle(title);
        setDescription(description);
        setTriggerableBy(triggerableBy);
        setProcesses(processes);
        setUpdatedAt(LocalDateTime.now());
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Set<String> getTriggerableBy() {
        return triggerableBy;
    }

    public boolean isAnyTrigger() {
        return anyTrigger;
    }

    public List<Process> getProcesses() {
        return processes;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getCreatedAt() {

        if (id == null) 
            throw new DomainException("Flow Definition not yet Created");

        return new ObjectId(id).getDate()
            .toInstant()
            .atZone(ZoneOffset.UTC)
            .toLocalDateTime();
    }

    public void setTitle(String title) {
        if (title == null || title.isBlank()) 
            throw new DomainException("FlowDefinition Title cannot be blank");

        this.title = title; 
    }

    public void setDescription(String description) {
        if (description == null || description.isBlank()) 
            throw new DomainException("FlowDefinition Description cannot be blank");

        this.description = description;
    }

    public void setTriggerableBy(Set<String> triggerableBy) {
        if (triggerableBy == null || triggerableBy.isEmpty()) {
            anyTrigger = true;    
        }else{
            this.triggerableBy = triggerableBy;
            anyTrigger = false;
        }
    }

    public void setProcesses(List<? extends Process> processes) {
        this.processes = List.copyOf(processes) ;
        for (int i = 0; i < processes.size(); i++) {
            Process current = this.processes.get(i);
            current.setStep(i);
        }
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
