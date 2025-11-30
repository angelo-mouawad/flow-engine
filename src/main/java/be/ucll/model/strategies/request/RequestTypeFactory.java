package be.ucll.model.strategies.request;

import java.util.HashMap;
import java.util.Map;

public class RequestTypeFactory {
    private static final Map<String, RequestType> REQUEST_TYPES = new HashMap<>();
    
    static {
        REQUEST_TYPES.put("ABSENCE_REQUEST", new AbsenceRequestType());
        REQUEST_TYPES.put("CLOCKIN_REQUEST", new ClockInRequestType());
    }
    
    public static RequestType fromTypeName(String typeName) {
        RequestType type = REQUEST_TYPES.get(typeName);
        if (type == null) {
            throw new IllegalArgumentException("Unknown request type: " + typeName);
        }
        return type;
    }
}
