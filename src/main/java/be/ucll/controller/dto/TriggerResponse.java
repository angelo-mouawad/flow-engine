package be.ucll.controller.dto;

import be.ucll.model.enums.FlowStatus;

public record TriggerResponse(
        String flowId,
        FlowStatus status
        ) {
}
