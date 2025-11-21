package be.ucll.controller.dto;

import java.util.List;

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
    @NotEmpty
    List<String> processes) {
}
