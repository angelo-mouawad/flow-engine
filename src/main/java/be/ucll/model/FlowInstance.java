package be.ucll.model;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import be.ucll.exception.DomainException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Document(collection = "FlowInstance")
public class FlowInstance {

    @Id
    private ObjectId id;
    @NotNull
    private FlowDefinition flowDefinition;
    @NotBlank
    private String title;
    @NotNull
    private Status status;

    private Process currentProcess;
    private LocalDate updatedAt;

    public FlowInstance(FlowDefinition flowDefinition, String title) {
        setFlowDefinition(flowDefinition);
        setTitle(title);
        setCurrentProcess(flowDefinition.getProcesses().get(0));
        setStatus(Status.ACTIVE);
    }

    public ObjectId getId() {
        return id;
    }

    public FlowDefinition getFlowDefinition() {
        return flowDefinition;
    }

    public Process getCurrentProcess() {
        return currentProcess;
    }

    public List<Process> getProcesses() {
        return flowDefinition.getProcesses();
    }

    public Status getStatus() {
        return status;
    }

    public LocalDate getCreatedAt() {
        if (id == null) 
            throw new DomainException("Flow Instance not yet Created");

        return id.getDate()
            .toInstant()
            .atZone(ZoneOffset.UTC)
            .toLocalDate();
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setTitle(String title) {
        if (title.isBlank()) 
            throw new DomainException("FlowInstance Title cannot be empty");

        this.title = title;
    }

    public void setCurrentProcess(Process currentProcess) {
        this.currentProcess = currentProcess;
    }

    public void setFlowDefinition(FlowDefinition flowDefinition) {
        if (flowDefinition == null) 
            throw new DomainException("Flow Instance requires a definition");
            
        this.flowDefinition = flowDefinition;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }
}
