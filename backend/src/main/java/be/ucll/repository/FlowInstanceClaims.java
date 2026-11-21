package be.ucll.repository;

public interface FlowInstanceClaims {

    /**
     * Moves a flow from PENDING to ACTIVE in a single atomic update. Returns true only for the one
     * caller that made the change, so two approvals arriving together can never both resume the flow.
     */
    boolean claimPending(String flowInstanceId);
}
