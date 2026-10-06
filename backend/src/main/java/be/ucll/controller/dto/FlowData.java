package be.ucll.controller.dto;

import java.util.Map;

import be.ucll.model.enums.RequestTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FlowData(
        @NotBlank String title,
        @NotBlank String triggeredBy,
        @NotNull Map<RequestTypeEnum, Map<String, Object>> data,
        String callbackUrl
) {
}
