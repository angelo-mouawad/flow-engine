package be.ucll.model.strategies.request;

import be.ucll.model.RequestData;
import be.ucll.model.enums.RequestStatus;
import be.ucll.model.enums.RequestTypeEnum;

public class MeetingRequestType implements RequestType {

    private static final String LABEL = "meeting request";

    @Override
    public RequestStatus validate(RequestData data) {
        data.requireText("Subject", LABEL);
        data.requireText("Location", LABEL);
        data.requireDateTime("Time", LABEL);
        data.requireText("Duration", LABEL);
        data.requireText("Submitted_By", LABEL);
        return RequestStatus.PENDING;
    }

    @Override
    public RequestTypeEnum getTypeName() {
        return RequestTypeEnum.MEETING_REQUEST;
    }
}
