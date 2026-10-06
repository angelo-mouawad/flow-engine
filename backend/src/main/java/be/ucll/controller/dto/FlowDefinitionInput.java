package be.ucll.controller.dto;

import java.util.List;
import java.util.Set;

import be.ucll.model.Process;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record FlowDefinitionInput(
        @NotBlank String title,
        @NotBlank String description,
        Set<String> triggerableBy,
        @NotEmpty List<Process> processes
) {
}
