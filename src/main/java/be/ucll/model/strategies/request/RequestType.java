package be.ucll.model.strategies.request;

import be.ucll.model.RequestData;
import be.ucll.model.RequestSubmission;

public interface RequestType {
    void validate(RequestData data);
    RequestSubmission process(RequestData data);
    String getRequestTypeName();
}
