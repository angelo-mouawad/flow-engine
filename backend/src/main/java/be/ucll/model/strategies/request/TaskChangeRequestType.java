package be.ucll.model.strategies.request;

import be.ucll.exception.DomainException;
import be.ucll.model.*;
import be.ucll.model.enums.RequestStatus;
import be.ucll.model.enums.RequestTypeEnum;

public class TaskChangeRequestType implements RequestType {
    
    @Override
    public RequestStatus validate(RequestData data) {
        String currentTask = data.getField("Current_Task", String.class);
        String newTask = data.getField("New_Task", String.class);
        String submittedBy = data.getField("Submitted_By", String.class);

        if (currentTask == null || currentTask.isBlank()) {
            throw new DomainException("new task is required for task change request");
        }

        if (newTask == null || newTask .isBlank()) {
            throw new DomainException("new task is required for task change request");
        }

        if (submittedBy == null || submittedBy.isBlank()) {
            throw new DomainException("User is required for absence request");
        }

        return RequestStatus.PENDING;
    }
 
    @Override
    public RequestTypeEnum getTypeName() {
        return RequestTypeEnum.TASK_CHANGE_REQUEST;
    }
}
