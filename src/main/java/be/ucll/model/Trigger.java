package be.ucll.model;

import jakarta.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Trigger")
public class Trigger {
    @Id
    private String id;

    @NotNull
    private TriggerType type;

    @NotNull
    private FlowDefinition[] flowDefinitions;

    public Trigger(String id, TriggerType type, FlowDefinition[] flowDefinitions) {
        this.id = id;
        this.type = type;
        this.flowDefinitions = flowDefinitions;
    }

    public String getId() {
        return id;
    }

    public TriggerType getType() {
        return type;
    }

    public FlowDefinition[] getFlowDefinitions() {
        return flowDefinitions;
    }
}
