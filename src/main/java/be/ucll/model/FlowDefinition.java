package be.ucll.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "FlowDefinition")
public class FlowDefinition {

    @Id
    private String id;
    private Process[] processes;
    private FlowInstance[] flowInstances;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    public FlowDefinition(Process[] processes, FlowInstance[] flowInstances, LocalDate createdAt) {
        this.processes = processes;
        this.flowInstances = flowInstances;
        this.createdAt = createdAt;
    }

     public void setId(String id) {
        this.id = id;
    }

    public void setProcesses(Process[] processes) {
        this.processes = processes;
    }

    public void setFlowInstances(FlowInstance[] flowInstances) {
        this.flowInstances = flowInstances;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public Process[] getProcesses() {
        return processes;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public FlowInstance[] getFlowInstances() {
        return flowInstances;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }
}
