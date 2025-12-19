package be.ucll.model.strategies.request;

import be.ucll.exception.DomainException;
import be.ucll.model.*;
import be.ucll.model.enums.RequestStatus;
import be.ucll.model.enums.RequestTypeEnum;

import java.time.LocalDate;
import java.time.ZonedDateTime;

public class ClockInRequestType implements RequestType {
    
    @Override
    public RequestStatus validate(RequestData data) {
        String startTimeStr = data.getField("Start_Time", String.class);
        String endTimeStr = data.getField("End_Time", String.class);
        String dateStr = data.getField("Date", String.class);
        String submittedBy = data.getField("Submitted_By", String.class);

        ZonedDateTime startTime =  ZonedDateTime.parse(startTimeStr);
        ZonedDateTime endTime =  ZonedDateTime.parse(endTimeStr);
        LocalDate date = LocalDate.parse(dateStr);

        if (date == null){
            throw new DomainException("date is required");
        }

        if (startTime == null || endTime == null) {
            throw new DomainException("Start time and end time are required");
        }
        
        if (startTime.isAfter(endTime)) {
            throw new DomainException("Start time cannot be after end time");
        }

        if (submittedBy == null || submittedBy.isBlank()) {
            throw new DomainException("User is required for absence request");
        }

        int startHour = startTime.getHour();
        int endHour = endTime.getHour();

        if (startHour >= 9 && endHour <= 17) {
            return RequestStatus.APPROVED;
        }

        return RequestStatus.PENDING;
    }
 
    @Override
    public RequestTypeEnum getTypeName() {
        return RequestTypeEnum.CLOCKIN_REQUEST;
    }
}
