package be.ucll.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import be.ucll.model.enums.ProcessType;

public class Approval extends Process{

    protected Approval() {}

    @JsonCreator
    public Approval(@JsonProperty("name") String name) {
        super(name, ProcessType.APPROVAL);
    }
}
