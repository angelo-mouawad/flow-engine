package be.ucll.model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import be.ucll.exception.DomainException;
import be.ucll.model.enums.FlowStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Document(collection = "FlowInstance")
public class FlowInstance {

    @Id
    private String id;

    @NotBlank
    private String title;

    @NotNull
    @JsonIgnore
    private FlowDefinition flowDefinition;

    @NotNull
    private FlowStatus flowStatus;

    private Map<String, Map<String, Object>> data = new HashMap<>();

    private int step;
    private LocalDateTime updatedAt;

    public FlowInstance(FlowDefinition flowDefinition, String title, Map<String, Map<String, Object>> data) {
        step = 0;
        setFlowDefinition(flowDefinition);
        setTitle(title);
        setFlowStatus(FlowStatus.ACTIVE);
        setUpdatedAt(LocalDateTime.now());
        this.data = data;
    }

    public String getId() {
        return id.toString();
    }

    public String getTitle() {
        return title;
    }

    public Map<String, Map<String, Object>> getData() {
        return data;
    }

    public String getFlowDefinitionId() {
        return flowDefinition.getId();
    }

    public FlowStatus getFlowStatus() {
        return flowStatus;
    }

    public int getStep() {
        return step;
    }

    public List<Process> getProcesses() {
        return flowDefinition.getProcesses();
    }

    @JsonIgnore
    public Process getCurrentProcess() {
        if(step >= flowDefinition.getProcesses().size()){
            return null;
        }

        return flowDefinition.getProcesses().get(step);
    }

    public LocalDateTime getCreatedAt() {
        if (id == null) 
            throw new DomainException("Flow Instance not yet Created");

        return new ObjectId(id).getDate()
            .toInstant()
            .atZone(ZoneOffset.UTC)
            .toLocalDateTime();
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void nextProcess(){
        step++;
    }

    private void setFlowDefinition(FlowDefinition flowDefinition) {
        if (flowDefinition == null) 
            throw new DomainException("Flow Instance requires a definition");
            
        this.flowDefinition = flowDefinition;
    }

    private void setTitle(String title) {
        if (title.isBlank()) 
            throw new DomainException("FlowInstance Title cannot be empty");

        this.title = title;
    }

    public void setFlowStatus(FlowStatus status) {
        this.flowStatus = status;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
