package be.ucll.model.strategies.request;

import be.ucll.exception.DomainException;
import be.ucll.model.RequestData;
import be.ucll.model.enums.RequestStatus;
import be.ucll.model.enums.RequestTypeEnum;

public class TaskChangeRequestType implements RequestType {

    private static final String LABEL = "task change request";

    @Override
    public RequestStatus validate(RequestData data) {
        String currentTask = data.requireText("Current_Task", LABEL);
        String newTask = data.requireText("New_Task", LABEL);
        data.requireText("Submitted_By", LABEL);

        if (currentTask.equalsIgnoreCase(newTask)) {
            throw new DomainException("New task must be different from the current task for " + LABEL);
        }
        return RequestStatus.PENDING;
    }

    @Override
    public RequestTypeEnum getTypeName() {
        return RequestTypeEnum.TASK_CHANGE_REQUEST;
    }
}
