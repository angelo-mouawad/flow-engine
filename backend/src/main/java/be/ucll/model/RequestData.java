package be.ucll.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;

import be.ucll.exception.DomainException;

public class RequestData {

    private Map<String, Object> fields = new LinkedHashMap<>();

    public RequestData() {
    }

    public RequestData(Map<String, Object> data) {
        if (data != null) {
            data.forEach(this::setField);
        }
    }

    public void setField(String key, Object value) {
        if (key != null) {
            fields.put(key, value);
        }
    }

    public Object getField(String key) {
        return fields.get(key);
    }

    public Map<String, Object> getAllFields() {
        return fields;
    }

    public String requireText(String key, String requestLabel) {
        Object value = fields.get(key);
        String text = value == null ? null : value.toString().trim();
        if (text == null || text.isEmpty()) {
            throw new DomainException(key + " is required for " + requestLabel);
        }
        return text;
    }

    public LocalDate requireDate(String key, String requestLabel) {
        String text = requireText(key, requestLabel);
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            throw new DomainException(key + " must be a date like 2025-11-10 for " + requestLabel);
        }
    }

    public OffsetDateTime requireDateTime(String key, String requestLabel) {
        String text = requireText(key, requestLabel);
        try {
            return OffsetDateTime.parse(text);
        } catch (DateTimeParseException e) {
            throw new DomainException(key + " must be a date and time like 2025-11-10T09:00:00+01:00 for " + requestLabel);
        }
    }
}
