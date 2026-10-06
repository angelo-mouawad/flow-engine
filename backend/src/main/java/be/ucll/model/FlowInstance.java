package be.ucll.model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import be.ucll.exception.DomainException;
import be.ucll.model.enums.FlowStatus;
import be.ucll.model.enums.RequestTypeEnum;

@Document(collection = "FlowInstance")
public class FlowInstance {

    @Id
    private String id;
    private String title;
    private String triggeredBy;
    private String callbackUrl;
    private FlowDefinition flowDefinition;
    private FlowStatus flowStatus;
    private Map<RequestTypeEnum, Map<String, Object>> data = new HashMap<>();
    private int step;
    private LocalDateTime updatedAt;

    @PersistenceCreator
    protected FlowInstance() {
    }

    public FlowInstance(FlowDefinition flowDefinition, String title, String triggeredBy,
                        Map<RequestTypeEnum, Map<String, Object>> data, String callbackUrl) {
        if (flowDefinition == null) {
            throw new DomainException("A flow instance needs a flow definition");
        }
        if (title == null || title.isBlank()) {
            throw new DomainException("Flow instance title cannot be blank");
        }
        if (triggeredBy == null || triggeredBy.isBlank()) {
            throw new DomainException("A flow instance needs to know who triggered it");
        }
        this.flowDefinition = flowDefinition;
        this.title = title.trim();
        this.triggeredBy = triggeredBy.trim().toLowerCase();
        this.data = data == null ? new HashMap<>() : new HashMap<>(data);
        this.callbackUrl = callbackUrl == null || callbackUrl.isBlank() ? null : callbackUrl.trim();
        this.step = 0;
        this.flowStatus = FlowStatus.ACTIVE;
        touch();
    }

    public Process getCurrentProcess() {
        List<Process> processes = getProcesses();
        return step < processes.size() ? processes.get(step) : null;
    }

    @JsonIgnore
    public Process getProcess(int index) {
        List<Process> processes = getProcesses();
        if (index < 0 || index >= processes.size()) {
            throw new DomainException("Flow has no process at step " + index);
        }
        return processes.get(index);
    }

    public Map<String, Object> getDataFor(RequestTypeEnum type) {
        return data.get(type);
    }

    public void nextProcess() {
        step++;
        touch();
    }

    public void setFlowStatus(FlowStatus flowStatus) {
        this.flowStatus = flowStatus;
        touch();
    }

    private void touch() {
        updatedAt = LocalDateTime.now();
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

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public String getFlowDefinitionId() {
        return flowDefinition.getId();
    }

    public String getFlowDefinitionTitle() {
        return flowDefinition.getTitle();
    }

    public List<Process> getProcesses() {
        return flowDefinition.getProcesses();
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        if (id == null || !ObjectId.isValid(id)) {
            return updatedAt;
        }
        return LocalDateTime.ofInstant(new ObjectId(id).getDate().toInstant(), ZoneOffset.UTC);
    }
}
