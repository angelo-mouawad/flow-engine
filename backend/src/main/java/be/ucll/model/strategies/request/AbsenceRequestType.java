package be.ucll.model.strategies.request;

import be.ucll.exception.DomainException;
import be.ucll.model.*;
import be.ucll.model.enums.RequestStatus;
import be.ucll.model.enums.RequestTypeEnum;

import java.time.LocalDate;

public class AbsenceRequestType implements RequestType {
 
    @Override
    public RequestStatus validate(RequestData data) {
        String startDateStr = data.getField("Start_Date", String.class);
        String endDateStr = data.getField("End_Date", String.class);
        String submittedBy = data.getField("Submitted_By", String.class);
        String reason = data.getField("Reason", String.class);

        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr); 

        if (startDate == null || endDate == null) {
            throw new DomainException("Start date and end date are required");
        }
 
        if (startDate.isAfter(endDate)) {
            throw new DomainException("Start date cannot be after end date");
        }
 
        if (reason == null || reason.isBlank()) {
            throw new DomainException("Reason is required for absence request");
        }

        if (submittedBy == null || submittedBy.isBlank()) {
            throw new DomainException("User is required for absence request");
        }

        return RequestStatus.PENDING;
    }
 
    @Override
    public RequestTypeEnum getTypeName() {
        return RequestTypeEnum.ABSENCE_REQUEST;
    }
}
