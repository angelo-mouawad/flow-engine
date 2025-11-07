package be.ucll.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Document(collection = "RequestSubmission")
public class RequestSubmission {
    
    @Id
    private ObjectId id;
    
    private String requestType;
    private RequestStatus status;
    private RequestData data;
    private LocalDate submittedAt;
    private LocalDate processedAt;
    
    private String flowInstanceId;
    public String getId() {
        return id.toString();
    }
    
    public String getRequestType() {
        return requestType;
    }
    
    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
    
    public RequestStatus getStatus() {
        return status;
    }
    
    public void setStatus(RequestStatus status) {
        this.status = status;
    }
    
    public RequestData getData() {
        return data;
    }
    
    public void setData(RequestData data) {
        this.data = data;
    }
    
    public LocalDate getSubmittedAt() {
        return submittedAt;
    }
    
    public void setSubmittedAt(LocalDate submittedAt) {
        this.submittedAt = submittedAt;
    }
    
    public LocalDate getProcessedAt() {
        return processedAt;
    }
    
    public void setProcessedAt(LocalDate processedAt) {
        this.processedAt = processedAt;
    }
    
    public String getFlowInstanceId() {
        return flowInstanceId;
    }
    
    public void setFlowInstanceId(String flowInstanceId) {
        this.flowInstanceId = flowInstanceId;
    }
}
