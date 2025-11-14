package be.ucll.model;

import be.ucll.model.strategies.request.*;

public class Request extends Process{

    private RequestType requestType;
    private RequestData data;
    private RequestSubmission submission;

    public Request(String title, RequestType requestType){
        super(title);
        this.requestType = requestType;
    }

    @Override
    public void execute() {
        requestType.validate(data);
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public RequestSubmission getSubmission() {
        return submission;
    }

    public RequestData getData() {
        return data;
    }

    public void setSubmission(RequestSubmission submission) {
        this.submission = submission;
    }

    public void setData(RequestData data) {
        this.data = data;
    }
}
