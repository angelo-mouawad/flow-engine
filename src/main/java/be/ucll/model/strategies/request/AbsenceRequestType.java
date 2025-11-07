package be.ucll.model.strategies.request;

import be.ucll.exception.DomainException;
import be.ucll.model.*;

import java.time.LocalDate;

public class AbsenceRequestType implements RequestType {
    
    @Override
    public void validate(RequestData data) {
        String startDateStr = data.getField("startDate", String.class);
        String endDateStr = data.getField("endDate", String.class);
        String submittedBy = data.getField("submittedBy", String.class);
        String reason = data.getField("reason", String.class);

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
    }
    
    @Override
    public RequestSubmission process(RequestData data) {
        validate(data);
        
        RequestSubmission submission = new RequestSubmission();
        submission.setRequestType(getRequestTypeName());
        submission.setStatus(RequestStatus.PENDING);
        submission.setData(data);
        submission.setSubmittedAt(LocalDate.now());
        
        return submission;
    }
    
    @Override
    public String getRequestTypeName() {
        return "ABSENCE_REQUEST";
    }
}
