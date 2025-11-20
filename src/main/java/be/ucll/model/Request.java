package be.ucll.model;

import java.time.LocalDate;

import be.ucll.model.strategies.request.*;

public class Request extends Process{

    private RequestType requestType;

    public Request(String name, RequestType requestType){
        super(name, ProcessType.REQUEST);
        this.requestType = requestType;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public RequestSubmission submit(RequestData requestData) {
        requestType.validate(requestData);
        
        RequestSubmission submission = new RequestSubmission();
        submission.setRequestType(requestType.getTypeName());
        submission.setStatus(RequestStatus.PENDING);
        submission.setData(requestData);
        submission.setSubmittedAt(LocalDate.now());
        
        return submission;
    }
}
