package be.ucll.model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import be.ucll.exception.DomainException;
import be.ucll.model.enums.RequestStatus;
import be.ucll.model.enums.RequestTypeEnum;

@Document(collection = "RequestSubmission")
public class RequestSubmission {

    @Id
    private String id;
    /** Optimistic lock: two approvers deciding at the same moment can no longer overwrite each other's vote. */
    @Version
    @JsonIgnore
    private Long version;
    private String flowInstanceId;
    private Request request;
    private RequestData requestData;
    private RequestStatus status;
    private List<ApprovalVote> votes = new ArrayList<>();
    private LocalDateTime submittedAt;
    private LocalDateTime processedAt;

    @PersistenceCreator
    protected RequestSubmission() {
    }

    public RequestSubmission(RequestData requestData, Request request, String flowInstanceId, RequestStatus status) {
        if (request == null) {
            throw new DomainException("A submission needs the request it belongs to");
        }
        this.request = request;
        this.requestData = requestData == null ? new RequestData() : requestData;
        this.flowInstanceId = flowInstanceId;
        this.submittedAt = LocalDateTime.now();

        if (request.isApprovable()) {
            request.getApprovableBy().stream()
                    .map(ApprovalVote::normalise)
                    .distinct()
                    .forEach(email -> votes.add(new ApprovalVote(email, RequestStatus.PENDING)));
        }

        this.status = status == null ? RequestStatus.PENDING : status;
        if (this.status != RequestStatus.PENDING) {
            this.processedAt = submittedAt;
        }
    }

    public void decide(String user, RequestStatus decision) {
        if (decision != RequestStatus.APPROVED && decision != RequestStatus.DECLINED) {
            throw new DomainException("A request can only be approved or declined");
        }
        if (status != RequestStatus.PENDING) {
            throw new DomainException("This request has already been " + status.name().toLowerCase());
        }

        ApprovalVote vote = voteOf(user)
                .orElseThrow(() -> new DomainException(user + " is not an approver for this request"));
        if (vote.getStatus() != RequestStatus.PENDING) {
            throw new DomainException(user + " has already responded to this request");
        }

        vote.setStatus(decision);

        int required = request.getMinApprovals();
        int stillPossible = votes.size() - countApprovals(RequestStatus.DECLINED);

        if (countApprovals(RequestStatus.APPROVED) >= required) {
            resolve(RequestStatus.APPROVED);
        } else if (stillPossible < required) {
            resolve(RequestStatus.DECLINED);
        }
    }

    private void resolve(RequestStatus outcome) {
        status = outcome;
        processedAt = LocalDateTime.now();
    }

    private Optional<ApprovalVote> voteOf(String user) {
        return votes.stream().filter(vote -> vote.isFor(user)).findFirst();
    }

    public int countApprovals(RequestStatus value) {
        return (int) votes.stream().filter(vote -> vote.getStatus() == value).count();
    }

    public boolean isPendingFor(String user) {
        return status == RequestStatus.PENDING
                && voteOf(user).map(vote -> vote.getStatus() == RequestStatus.PENDING).orElse(false);
    }

    public boolean isResolved() {
        return status == RequestStatus.APPROVED || status == RequestStatus.DECLINED;
    }

    public String getId() {
        return id;
    }

    public String getFlowInstanceId() {
        return flowInstanceId;
    }

    public RequestStatus getStatus() {
        return status;
    }

    @JsonProperty("data")
    public RequestData getRequestData() {
        return requestData;
    }

    /** Each approver's answer, keyed by email in the order the definition lists them. */
    @JsonProperty("approvals")
    public Map<String, RequestStatus> getApprovals() {
        Map<String, RequestStatus> readable = new LinkedHashMap<>();
        for (String email : request.getApprovableBy()) {
            voteOf(email).ifPresent(vote -> readable.putIfAbsent(email, vote.getStatus()));
        }
        return readable;
    }

    @JsonIgnore
    public Request getRequest() {
        return request;
    }

    public String getRequestName() {
        return request.getName();
    }

    public int getRequestStep() {
        return request.getStep();
    }

    public RequestTypeEnum getRequestTypeName() {
        return request.getRequestTypeName();
    }

    public int getMinApprovals() {
        return request.getMinApprovals();
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public LocalDateTime getCreatedAt() {
        if (id == null || !ObjectId.isValid(id)) {
            return submittedAt;
        }
        return LocalDateTime.ofInstant(new ObjectId(id).getDate().toInstant(), ZoneOffset.UTC);
    }
}
