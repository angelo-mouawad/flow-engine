package be.ucll.controller.dto;

import java.util.List;

import be.ucll.model.Process;
import jakarta.validation.constraints.NotNull;

public record FlowDefinitionInput(
    @NotNull
    String title,
    @NotNull
    String description,
    @NotNull
    List<Process> processes
    ) {
}
