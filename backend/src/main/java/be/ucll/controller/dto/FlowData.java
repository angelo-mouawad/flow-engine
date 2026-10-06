package be.ucll.controller.dto;

import java.util.Map;

import be.ucll.model.enums.RequestTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record FlowData(
    @NotNull
    @NotBlank
    String title,
    @NotNull
    @NotBlank
    String triggeredBy,
    @NotNull
    @NotEmpty
    Map<RequestTypeEnum, Map<String, Object>> data
        ) {
}
