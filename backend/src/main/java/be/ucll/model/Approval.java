package be.ucll.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import be.ucll.model.enums.ProcessType;

public class Approval extends Process{

    private Set<Integer> requestSteps;

    protected Approval() {}

    @JsonCreator
    public Approval(@JsonProperty("name") String name, Set<Integer> requestStep) {
        super(name, ProcessType.APPROVAL);
        setRequestStep(requestStep);
    }

    public Set<Integer> getRequestSteps() {
        return requestSteps;
    }

    public void setRequestStep(Set<Integer> requestStep) {
        this.requestSteps = requestStep;
    }
}
