package be.ucll.model;

import org.springframework.data.annotation.PersistenceCreator;

import be.ucll.model.enums.RequestStatus;

/**
 * One approver's answer on a submission. The email is stored as a value rather than a map key, so
 * addresses that only differ in dots and underscores stay separate approvers.
 */
public class ApprovalVote {

    private String email;
    private RequestStatus status;

    @PersistenceCreator
    protected ApprovalVote() {
    }

    public ApprovalVote(String email, RequestStatus status) {
        this.email = normalise(email);
        this.status = status;
    }

    public static String normalise(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    public boolean isFor(String user) {
        return email.equals(normalise(user));
    }

    public String getEmail() {
        return email;
    }

    public RequestStatus getStatus() {
        return status;
    }

    void setStatus(RequestStatus status) {
        this.status = status;
    }
}
