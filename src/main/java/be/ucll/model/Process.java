package be.ucll.model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import be.ucll.model.enums.ProcessType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "processType"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Request.class, name = "REQUEST"),
    @JsonSubTypes.Type(value = Approval.class, name = "APPROVAL"),
    @JsonSubTypes.Type(value = Notification.class, name = "NOTIFICATION")
})
public abstract class Process {

    @NotNull @NotBlank 
    private String name;

    @NotNull private ProcessType processType;

    @JsonIgnore
    private String flowInstanceId;

    protected Process() {}

    public Process(String name, ProcessType processType){ 
        this.name = name;
        setProcessType(processType);
    }

    public String getName() {
        return this.name;
    }

    public String getFlowInstanceId() {
        return flowInstanceId;
    }

    public ProcessType getProcessType() {
        return processType;
    }

    public void setFlowInstanceId(String flowInstanceId) {
        this.flowInstanceId = flowInstanceId;
    }

    private void setProcessType(ProcessType processType) {
        this.processType = processType;
    }

    public LocalDateTime getCreatedAt() {
        if (flowInstanceId == null) return null;

        return new ObjectId(flowInstanceId).getDate()
            .toInstant()
            .atZone(ZoneOffset.UTC)
            .toLocalDateTime();
    }
}
