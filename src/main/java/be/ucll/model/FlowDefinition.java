package be.ucll.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@Table(name = flow_definition")
public class FlowDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(min = 1)
    private Process[] processes;

    @NotNull
    @Size(min = 1)
    private FlowInstance[] flowInstances;

    @NotNull
    private LocalDate createdAt;

    @NotNull
    private LocalDate updatedAt;

    protected FlowDefinition() {}

    public FlowDefinition(@NotNull @Size(min = 1) Process[] processes, FlowInstance[] flowInstances, LocalDate createdAt) {
        this.processes = processes;
        this.flowInstances = flowInstances;
        this.createdAt = createdAt;
    }

     public void setId(long id) {
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

    public long getId() {
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