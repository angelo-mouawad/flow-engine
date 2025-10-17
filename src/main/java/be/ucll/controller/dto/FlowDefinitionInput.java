package be.ucll.controller.dto;

import jakarta.validation.constraints.NotNull;

public record FlowDefinitionInput(
    @NotNull
    String title,
    @NotNull
    String description
    ) {
}
