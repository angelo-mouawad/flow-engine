package be.ucll.model;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import be.ucll.exception.DomainException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

@Document(collection = "FlowDefinition")
public class FlowDefinition {

    @Id
    private ObjectId id;
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotEmpty
    @DBRef
    private List<Process> processes;

    private List<FlowInstance> flowInstances = new ArrayList<FlowInstance>();;
    private LocalDate updatedAt;

    public FlowDefinition(String title, String description, List<Process> processes) {
        setTitle(title);
        setDescription(description);
        setProcesses(processes);
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ObjectId getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<Process> getProcesses() {
        return processes;
    }

    public List<FlowInstance> getFlowInstances() {
        return flowInstances;
    }

    public LocalDate getCreatedAt() {
        if (id == null) 
            throw new DomainException("Flow Definition not yet Created");

        return id.getDate()
            .toInstant()
            .atZone(ZoneOffset.UTC)
            .toLocalDate();
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProcesses(List<Process> processes) {
        this.processes = processes;
    }

    public void setFlowInstances(List<FlowInstance> flowInstances) {
        this.flowInstances = flowInstances;
    }
}
