package be.ucll.model.strategies.request;

import java.time.ZonedDateTime;

import be.ucll.exception.DomainException;
import be.ucll.model.*;
import be.ucll.model.enums.RequestStatus;
import be.ucll.model.enums.RequestTypeEnum;

public class MeetingRequest implements RequestType {
    
    @Override
    public RequestStatus validate(RequestData data) {
        String subject = data.getField("Subject", String.class);
        String location = data.getField("Location", String.class);
        String timeStr = data.getField("Time", String.class);
        String duration = data.getField("Duration", String.class);
        String submittedBy = data.getField("Submitted_By", String.class);

        ZonedDateTime time =  ZonedDateTime.parse(timeStr);

        if (subject == null || subject.isBlank()) {
            throw new DomainException("subject is required for meeting request");
        }

        if (location == null || location.isBlank()) {
            throw new DomainException("location is required for meeting request");
        }

        if (duration == null || duration.isBlank()) {
            throw new DomainException("duration is required for meeting request");
        }

        if (time == null) {
            throw new DomainException("time is required for meeting request");
        }

        if (submittedBy == null || submittedBy.isBlank()) {
            throw new DomainException("User is required for meeting request");
        }

        return RequestStatus.PENDING;
    }
 
    @Override
    public RequestTypeEnum getTypeName() {
        return RequestTypeEnum.MEETING_REQUEST;
    }
}

