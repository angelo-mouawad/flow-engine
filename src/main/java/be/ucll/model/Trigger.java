package be.ucll.model;

public class Trigger {
    private Long id;
    private Type type;
    private FlowDefinition[] flowDefinitions;

    public enum Type {
        //SOMETHING
    }

    // Constructors, getters, and setters are usually needed
    public Trigger(Long id, Type type, FlowDefinition[] flowDefinitions) {
        this.id = id;
        this.type = type;
        this.flowDefinitions = flowDefinitions;
    }

    public Long getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public FlowDefinition[] getFlowDefinitions() {
        return flowDefinitions;
    }
}