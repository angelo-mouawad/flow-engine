package be.ucll.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import be.ucll.model.enums.ProcessType;
import be.ucll.model.enums.RequestTypeEnum;
import be.ucll.model.strategies.request.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class Request extends Process{

    @JsonIgnore
    private RequestType requestType;

    private boolean approvable = false;
    private int minApprovals = 0;
    private String[] approvableBy = {};

    @NotNull
    @NotBlank(message = "requestTypeName is required")
    private RequestTypeEnum requestTypeName;

    public Request(String name, RequestTypeEnum requestTypeName, boolean approvable, String[] approvableBy, int minApprovals){
        super(name, ProcessType.REQUEST);
        setRequestType(requestTypeName);
        this.approvable = approvable;
        setApprovableBy(approvableBy);
        setMinApprovals(minApprovals);
    }

    public RequestSubmission submit(RequestData requestData) {
        requestType.validate(requestData);
        
        return new RequestSubmission(
                requestData, 
                this);
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public RequestTypeEnum getRequestTypeName(){
        return requestTypeName;
    }

    public boolean isApprovable() {
        return approvable;
    }

    public int getMinApprovals() {
        return minApprovals;
    }

    public String[] getApprovableBy() {
        return approvableBy;
    }

    public void setRequestType(RequestTypeEnum requestTypeName) {
        this.requestTypeName = requestTypeName;
        this.requestType = RequestTypeFactory.fromTypeName(requestTypeName);
    }

    public void setMinApprovals(int minApprovals) {
        if (approvable) {
            this.minApprovals = minApprovals == 0 ? approvableBy.length : minApprovals;
        }
    }

    public void setApprovableBy(String[] approvableBy) {
        if (approvable) {
            this.approvableBy = approvableBy;
        }
    }
}
