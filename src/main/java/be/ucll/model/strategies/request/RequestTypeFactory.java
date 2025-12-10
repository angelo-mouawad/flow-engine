package be.ucll.model.strategies.request;

import java.util.HashMap;
import java.util.Map;

import be.ucll.model.enums.RequestTypeEnum;

public class RequestTypeFactory {
    private static final Map<RequestTypeEnum, RequestType> REQUEST_TYPES = new HashMap<>();
    
    static {
        REQUEST_TYPES.put(
                RequestTypeEnum.ABSENCE_REQUEST, 
                new AbsenceRequestType());
        REQUEST_TYPES.put(
                RequestTypeEnum.CLOCKIN_REQUEST, 
                new ClockInRequestType());
    }
    
    public static RequestType fromTypeName(RequestTypeEnum typeName) {
        RequestType type = REQUEST_TYPES.get(typeName);
        if (type == null) {
            throw new IllegalArgumentException("Unknown request type: " + typeName);
        }
        return type;
    }
}
