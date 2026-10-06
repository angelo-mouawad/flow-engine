package be.ucll.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import be.ucll.model.enums.ProcessType;

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

    private String name;
    private ProcessType processType;
    private int step;

    protected Process() {
    }

    protected Process(String name, ProcessType processType) {
        this.name = name == null ? null : name.trim();
        this.processType = processType;
    }

    public String getName() {
        return name;
    }

    public ProcessType getProcessType() {
        return processType;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    @JsonIgnore
    public String getLabel() {
        return name == null || name.isBlank() ? processType + " at step " + step : "'" + name + "'";
    }
}
