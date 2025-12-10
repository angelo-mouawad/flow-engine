package be.ucll.controller.dto;

import java.util.Map;

import be.ucll.model.enums.RequestTypeEnum;

public record FlowData(
    String title,
    String triggeredBy,
    Map<RequestTypeEnum, Map<String, Object>> data
        ) {
}
