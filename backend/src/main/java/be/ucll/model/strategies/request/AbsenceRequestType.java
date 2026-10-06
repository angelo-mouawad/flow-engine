package be.ucll.model.strategies.request;

import java.time.LocalDate;

import be.ucll.exception.DomainException;
import be.ucll.model.RequestData;
import be.ucll.model.enums.RequestStatus;
import be.ucll.model.enums.RequestTypeEnum;

public class AbsenceRequestType implements RequestType {

    private static final String LABEL = "absence request";

    @Override
    public RequestStatus validate(RequestData data) {
        LocalDate startDate = data.requireDate("Start_Date", LABEL);
        LocalDate endDate = data.requireDate("End_Date", LABEL);
        data.requireText("Reason", LABEL);
        data.requireText("Submitted_By", LABEL);

        if (startDate.isAfter(endDate)) {
            throw new DomainException("Start date cannot be after end date for " + LABEL);
        }
        return RequestStatus.PENDING;
    }

    @Override
    public RequestTypeEnum getTypeName() {
        return RequestTypeEnum.ABSENCE_REQUEST;
    }
}
