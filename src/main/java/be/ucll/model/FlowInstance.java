package be.ucll.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class FlowInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private FlowDefinition flowDefinition;
    private Process currentProcess;
    private List<Process> processes = new ArrayList<>();
    private Date createdAt;
    private Date updatedAt;

    protected FlowInstance() {
    }

    public FlowInstance(Date createdAt, Process currentProcess, FlowDefinition flowDefinition, List<Process> processes, Date updatedAt) {
        setCreatedAt(createdAt);
        setCurrentProcess(currentProcess);
        setFlowDefinition(flowDefinition);
        setProcesses(processes);
        setUpdatedAt(updatedAt);
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Process getCurrentProcess() {
        return currentProcess;
    }

    public void setCurrentProcess(Process currentProcess) {
        this.currentProcess = currentProcess;
    }

    public FlowDefinition getFlowDefinition() {
        return flowDefinition;
    }

    public void setFlowDefinition(FlowDefinition flowDefinition) {
        this.flowDefinition = flowDefinition;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Process> getProcesses() {
        return processes;
    }

    public void setProcesses(List<Process> processes) {
        this.processes = processes;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
