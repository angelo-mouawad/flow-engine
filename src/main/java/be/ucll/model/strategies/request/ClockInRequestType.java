package be.ucll.model.strategies.request;

import be.ucll.exception.DomainException;
import be.ucll.model.*;
import be.ucll.model.enums.RequestTypeEnum;

import java.time.ZonedDateTime;

public class ClockInRequestType implements RequestType {
    
    @Override
    public void validate(RequestData data) {
        String startTimeStr = data.getField("startTime", String.class);
        String endTimeStr = data.getField("endTime", String.class);
        String submittedBy = data.getField("submittedBy", String.class);

        ZonedDateTime startTime =  ZonedDateTime.parse(startTimeStr);
        ZonedDateTime endTime =  ZonedDateTime.parse(endTimeStr);

        if (startTime == null || endTime == null) {
            throw new DomainException("Start time and end time are required");
        }
        
        if (startTime.isAfter(endTime)) {
            throw new DomainException("Start time cannot be after end time");
        }
        
        if (submittedBy == null || submittedBy.isBlank()) {
            throw new DomainException("User is required for absence request");
        }
    }
 
    @Override
    public RequestTypeEnum getTypeName() {
        return RequestTypeEnum.CLOCKIN_REQUEST;
    }
}
