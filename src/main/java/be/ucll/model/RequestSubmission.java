package be.ucll.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import be.ucll.exception.DomainException;
import be.ucll.model.enums.RequestStatus;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Document(collection = "RequestSubmission")
public class RequestSubmission {

    @Id
    private String id;

    @JsonIgnore
    private Map<String, RequestStatus> approvals = new HashMap<>();

    private RequestStatus status;
    private RequestData requestData;

    @JsonIgnore
    private Request request;

    private LocalDateTime submittedAt;
    private LocalDateTime processedAt;

    public RequestSubmission(RequestData requestData, Request request){
        setData(requestData);
        setRequest(request);
        setSubmittedAt(LocalDateTime.now());
    }

    public void approve(String user, RequestStatus status){
        if(!approvals.containsKey(user))
            throw new DomainException(user + "is not in the list of needed approvals");

        approvals.put(user, status);

        int min = request.getMinApprovals();
        int size = approvals.size();

        if(getApprovalStatusTotal(RequestStatus.APPROVED) >= min){
            setStatus(RequestStatus.APPROVED);
        }else if (size - getApprovalStatusTotal(RequestStatus.DECLINED) < min) {
            setStatus(RequestStatus.DECLINED);
        }    
    }

    public String getId() {
        return id.toString();
    }

    public int getApprovalStatusTotal(RequestStatus status){
        return Collections.frequency(approvals.values(), status);
    }

    public void setApprovals() {

        if(!request.isApprovable()){
            return;
        } 

        for (String email: request.getApprovableBy()) {
            email = email.replace('.', '_');
            approvals.put(email, RequestStatus.PENDING);
        }
    }

    public RequestStatus getStatus() {
        return status;
    }

    public boolean isPendingForUser(String user){
        if (!approvals.containsKey(user)) 
            return false;

        return approvals.get(user) == RequestStatus.PENDING;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public boolean isApprovable() {
        return request.isApprovable();
    }

    public RequestData getData() {
        return requestData;
    }

    public void setData(RequestData requestData) {
        this.requestData = requestData;
    }

    public Map<String, RequestStatus> getApprovals() {
        return approvals;
    }
    
    public void setRequest(Request request) {
        this.request = request;
        if (request.isApprovable()) {
            setApprovals();
            setStatus(RequestStatus.PENDING);
        }else{
            setStatus(RequestStatus.APPROVED);
        } 
    }

    public Request getRequest() {
        return request;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public String getFlowInstanceId() {
        return request.getFlowInstanceId();
    }

    public LocalDateTime getCreatedAt() {

        if (id == null) 
            throw new DomainException("Flow Definition not yet Created");

        return new ObjectId(id).getDate()
            .toInstant()
            .atZone(ZoneOffset.UTC)
            .toLocalDateTime();
    }
}
