package be.ucll.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import be.ucll.exception.DomainException;
import be.ucll.model.enums.ProcessType;
import be.ucll.model.enums.RequestStatus;
import be.ucll.model.enums.RequestTypeEnum;
import be.ucll.model.strategies.request.RequestType;
import be.ucll.model.strategies.request.RequestTypeFactory;

public class Request extends Process {

    private RequestTypeEnum requestTypeName;
    private boolean approvable;
    private int minApprovals;
    private List<String> approvableBy = new ArrayList<>();

    protected Request() {
    }

    @JsonCreator
    public Request(@JsonProperty("name") String name,
                   @JsonProperty("requestTypeName") RequestTypeEnum requestTypeName,
                   @JsonProperty("approvable") Boolean approvable,
                   @JsonProperty("approvableBy") List<String> approvableBy,
                   @JsonProperty("minApprovals") Integer minApprovals) {
        super(name, ProcessType.REQUEST);
        if (requestTypeName == null) {
            throw new DomainException("Every request process needs a request type");
        }
        this.requestTypeName = requestTypeName;
        this.approvable = Boolean.TRUE.equals(approvable);

        if (this.approvable) {
            this.approvableBy = normaliseEmails(approvableBy);
            this.minApprovals = minApprovals == null || minApprovals == 0 ? this.approvableBy.size() : minApprovals;
        }
    }

    public RequestSubmission submit(RequestData requestData, String flowInstanceId) {
        RequestStatus validationResult = getRequestType().validate(requestData);
        RequestStatus status = approvable ? validationResult : RequestStatus.APPROVED;
        return new RequestSubmission(requestData, this, flowInstanceId, status);
    }

    public void validate() {
        if (!approvable) {
            return;
        }
        if (approvableBy.isEmpty()) {
            throw new DomainException("Request " + getLabel() + " needs at least one approver");
        }
        if (minApprovals < 1 || minApprovals > approvableBy.size()) {
            throw new DomainException("Request " + getLabel() + " needs between 1 and "
                    + approvableBy.size() + " approvals, not " + minApprovals);
        }
    }

    @JsonIgnore
    public RequestType getRequestType() {
        return RequestTypeFactory.fromTypeName(requestTypeName);
    }

    public RequestTypeEnum getRequestTypeName() {
        return requestTypeName;
    }

    public boolean isApprovable() {
        return approvable;
    }

    public int getMinApprovals() {
        return minApprovals;
    }

    public List<String> getApprovableBy() {
        return List.copyOf(approvableBy);
    }

    static List<String> normaliseEmails(List<String> emails) {
        if (emails == null) {
            return new ArrayList<>();
        }
        LinkedHashSet<String> unique = new LinkedHashSet<>();
        for (String email : emails) {
            if (email != null && !email.isBlank()) {
                unique.add(email.trim().toLowerCase());
            }
        }
        return new ArrayList<>(unique);
    }
}
