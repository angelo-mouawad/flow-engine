package be.ucll.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Table(name = "flow_definition")
@Entity
public class FlowDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Size(min = 1)
    private List<String> processes = new ArrayList<>();
    @NotNull
    @Size(min = 1)
    private List<String> flowInstances = new ArrayList<>();
    private Date createdAt;
    private Date updatedAt;

    public FlowDefinition() {}

    public FlowDefinition(Date createdAt, List<String> flowInstances, List<String> processes, Date updatedAt) {
        setCreatedAt(createdAt);
        setFlowInstances(flowInstances);
        setProcesses(processes);
        setUpdatedAt(updatedAt);
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getFlowInstances() {
        return flowInstances;
    }

    public void setFlowInstances(List<String> flowInstances) {
        this.flowInstances = flowInstances;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getProcesses() {
        return processes;
    }

    public void setProcesses(List<String> processes) {
        this.processes = processes;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}