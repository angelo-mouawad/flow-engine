package be.ucll.model.strategies.request;

import java.time.LocalTime;
import java.time.OffsetDateTime;

import be.ucll.exception.DomainException;
import be.ucll.model.RequestData;
import be.ucll.model.enums.RequestStatus;
import be.ucll.model.enums.RequestTypeEnum;

public class ClockInRequestType implements RequestType {

    private static final String LABEL = "clock-in request";
    private static final LocalTime OFFICE_OPENS = LocalTime.of(9, 0);
    private static final LocalTime OFFICE_CLOSES = LocalTime.of(17, 0);

    @Override
    public RequestStatus validate(RequestData data) {
        data.requireDate("Date", LABEL);
        OffsetDateTime startTime = data.requireDateTime("Start_Time", LABEL);
        OffsetDateTime endTime = data.requireDateTime("End_Time", LABEL);
        data.requireText("Submitted_By", LABEL);

        if (!startTime.isBefore(endTime)) {
            throw new DomainException("Start time must be before end time for " + LABEL);
        }

        boolean sameDay = startTime.toLocalDate().equals(endTime.toLocalDate());
        boolean insideOfficeHours = !startTime.toLocalTime().isBefore(OFFICE_OPENS)
                && !endTime.toLocalTime().isAfter(OFFICE_CLOSES);

        return sameDay && insideOfficeHours ? RequestStatus.APPROVED : RequestStatus.PENDING;
    }

    @Override
    public RequestTypeEnum getTypeName() {
        return RequestTypeEnum.CLOCKIN_REQUEST;
    }
}
