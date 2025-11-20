package be.ucll.model.strategies.request;

import be.ucll.model.RequestData;

public interface RequestType {
    void validate(RequestData data);
    String getTypeName();
}
