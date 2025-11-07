package be.ucll.model;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;

import be.ucll.exception.DomainException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Document(collection = "FlowInstance")
public class FlowInstance {

    @Id
    private ObjectId id;

    @NotNull
    @DBRef
    private FlowDefinition flowDefinition;

    @NotBlank
    private String title;

    @NotNull
    private FlowStatus flowStatus;

    private int step;
    private LocalDate updatedAt;

    public FlowInstance(FlowDefinition flowDefinition, String title) {
        step = 0;
        setFlowDefinition(flowDefinition);
        setTitle(title);
        setFlowStatus(FlowStatus.ACTIVE);
        setUpdatedAt(LocalDate.now());
    }

    public String getId() {
        return id.toString();
    }

    public String getTitle() {
        return title;
    }

    public String getTitle() {
        return title;
    }

    public FlowDefinition getFlowDefinition() {
        return flowDefinition;
    }

    public Process getCurrentProcess() {
        if(step >= flowDefinition.getProcesses().size()){
            return null;
        }

        return flowDefinition.getProcesses().get(step);
    }

    public void nextProcess(){
        step++;
    }

    public List<Process> getProcesses() {
        return flowDefinition.getProcesses();
    }

    public FlowStatus getFlowStatus() {
        return flowStatus;
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

    public void setFlowDefinition(FlowDefinition flowDefinition) {
        if (flowDefinition == null) 
            throw new DomainException("Flow Instance requires a definition");
            
        this.flowDefinition = flowDefinition;
    }

    public void setFlowStatus(FlowStatus status) {
        this.flowStatus = status;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }
}
