package be.ucll.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Trigger")
public class Trigger {
    @Id
    private String id;
    private TriggerType type;
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
