package be.ucll.model.strategies.request;

import be.ucll.model.RequestData;
import be.ucll.model.enums.RequestTypeEnum;

public interface RequestType {
    void validate(RequestData data);
    RequestTypeEnum getTypeName();
}
