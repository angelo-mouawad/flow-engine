package be.ucll.model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import be.ucll.exception.DomainException;
import be.ucll.model.enums.FlowStatus;
import be.ucll.model.enums.RequestTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Document(collection = "FlowInstance")
public class FlowInstance {

    @Id
    private String id;

    @NotBlank
    private String title;

    @NotNull
    private String triggeredBy;

    @NotNull
    private String callingURL;

    @NotNull @JsonIgnore
    private FlowDefinition flowDefinition;

    @NotNull
    private FlowStatus flowStatus;

    @JsonIgnore
    private List<RequestSubmission> submissions = new ArrayList<>();

    private Map<RequestTypeEnum, Map<String, Object>> data = new HashMap<>();

    private int step;
    private LocalDateTime updatedAt;

    public FlowInstance(FlowDefinition flowDefinition, String title, String triggeredBy, Map<RequestTypeEnum, Map<String, Object>> data, String callingURL) {
        step = 0;
        setFlowDefinition(flowDefinition);
        setTitle(title);
        setTriggeredBy(triggeredBy);
        setCallingURL(callingURL);
        setFlowStatus(FlowStatus.ACTIVE);
        setUpdatedAt(LocalDateTime.now());
        this.data = data;
    }

    public String getId() { 
        return id; 
    }

    public String getTitle() { 
        return title; 
    }

    public String getTriggeredBy() { 
        return triggeredBy; 
    }

    public String getCallingURL() {
        return callingURL;
    }

    public String getFlowDefinitionId() {
        return flowDefinition.getId();
    }

    public Map<RequestTypeEnum, Map<String, Object>> getData() {
        return data;
    }

    public FlowStatus getFlowStatus() {
        return flowStatus;
    }

    public int getStep() {
        return step;
    }

    public List<RequestSubmission> getSubmissions() {
        return submissions;
    }

    public List<Process> getProcesses() {
        return flowDefinition.getProcesses();
    }

    public Process getCurrentProcess() {
        if(step >= getProcesses().size()){
            return null;
        }

        return getProcesses().get(step);
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

    public void nextProcess() { step++; }

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

    public void addSubmission(RequestSubmission submission) {
        submissions.add(submission);
    }

    public void setTriggeredBy(String triggeredBy) {
        this.triggeredBy = triggeredBy != null? triggeredBy: null;
    }

    public void setCallingURL(String callingURL) {
        this.callingURL = callingURL;
    }

    public void setFlowStatus(FlowStatus status) {
        this.flowStatus = status;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
