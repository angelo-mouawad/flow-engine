package be.ucll.controller.dto;

import java.util.List;
import java.util.Set;

import org.springframework.validation.annotation.Validated;

import be.ucll.model.Process;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record FlowDefinitionInput(
    @NotNull
    @NotBlank
    String title,
    @NotNull
    @NotBlank
    String description,
    @NotNull
    Set<String> triggerableBy,
    @NotEmpty
    List<? extends Process> processes) {
}
