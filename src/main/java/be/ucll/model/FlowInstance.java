package be.ucll.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "FlowInstance")
public class FlowInstance {

    @Id
    private String id;
    private FlowDefinition flowDefinition;
    private Process currentProcess;
    private List<Process> processes = new ArrayList<>();
    private Date createdAt;
    private Date updatedAt;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
