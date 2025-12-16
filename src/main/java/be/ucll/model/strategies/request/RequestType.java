package be.ucll.model.strategies.request;

import be.ucll.model.RequestData;
import be.ucll.model.enums.RequestStatus;
import be.ucll.model.enums.RequestTypeEnum;

public interface RequestType {
    RequestStatus validate(RequestData data);
    RequestTypeEnum getTypeName();
}
