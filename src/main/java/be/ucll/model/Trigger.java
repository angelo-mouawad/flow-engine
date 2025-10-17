package be.ucll.model;

import jakarta.validation.constraints.*;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Trigger")
public class Trigger {
    @Id
    private ObjectId id;
    @NotNull
    private TriggerType type;
    @NotNull
    private FlowDefinition[] flowDefinitions;

    public Trigger(TriggerType type, FlowDefinition[] flowDefinitions) {
        this.type = type;
        this.flowDefinitions = flowDefinitions;
    }

    public ObjectId getId() {
        return id;
    }

    public TriggerType getType() {
        return type;
    }

    public FlowDefinition[] getFlowDefinitions() {
        return flowDefinitions;
    }
}
