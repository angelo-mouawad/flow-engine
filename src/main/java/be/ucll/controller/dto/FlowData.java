package be.ucll.controller.dto;

import java.util.Map;

public record FlowData(
    String title,
    Map<String, Map<String, Object>> data
        ) {
}
