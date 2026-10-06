package be.ucll.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import be.ucll.model.enums.ProcessType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "processType",
    visible = true
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

    private int step = 0;

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

    public int getStep() {
        return step;
    }

    public void setFlowInstanceId(String flowInstanceId) {
        this.flowInstanceId = flowInstanceId;
    }

    private void setProcessType(ProcessType processType) {
        this.processType = processType;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
