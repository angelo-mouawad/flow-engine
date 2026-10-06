package be.ucll.model;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import be.ucll.model.enums.ProcessType;

public class Approval extends Process {

    private Set<Integer> requestSteps = new TreeSet<>();

    protected Approval() {
    }

    @JsonCreator
    public Approval(@JsonProperty("name") String name,
                    @JsonProperty("requestSteps") Set<Integer> requestSteps) {
        super(name, ProcessType.APPROVAL);
        if (requestSteps != null) {
            requestSteps.stream().filter(step -> step != null).forEach(this.requestSteps::add);
        }
    }

    public Set<Integer> getRequestSteps() {
        return Collections.unmodifiableSortedSet(new TreeSet<>(requestSteps));
    }
}
