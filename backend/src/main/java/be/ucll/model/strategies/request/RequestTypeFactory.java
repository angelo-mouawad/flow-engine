package be.ucll.model.strategies.request;

import java.util.EnumMap;
import java.util.Map;

import be.ucll.exception.DomainException;
import be.ucll.model.enums.RequestTypeEnum;

public final class RequestTypeFactory {

    private static final Map<RequestTypeEnum, RequestType> REQUEST_TYPES = new EnumMap<>(RequestTypeEnum.class);

    static {
        register(new AbsenceRequestType());
        register(new ClockInRequestType());
        register(new TaskChangeRequestType());
        register(new MeetingRequestType());
    }

    private RequestTypeFactory() {
    }

    private static void register(RequestType type) {
        REQUEST_TYPES.put(type.getTypeName(), type);
    }

    public static RequestType fromTypeName(RequestTypeEnum typeName) {
        if (typeName == null) {
            throw new DomainException("Request type is required");
        }
        RequestType type = REQUEST_TYPES.get(typeName);
        if (type == null) {
            throw new DomainException("Unknown request type: " + typeName);
        }
        return type;
    }
}
