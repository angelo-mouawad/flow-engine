package be.ucll.controller.dto;

import java.util.List;

import be.ucll.model.Process;
import be.ucll.model.Trigger;
import jakarta.validation.constraints.NotNull;

public record FlowDefinitionInput(
    @NotNull
    String title,
    @NotNull
    String description,
    @NotNull
    List<Process> processes,
    Trigger trigger
    ) {
}
