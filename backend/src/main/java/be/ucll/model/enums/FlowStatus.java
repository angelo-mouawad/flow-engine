package be.ucll.model.enums;

public enum FlowStatus {
    ACTIVE,
    PENDING,
    SUCCESS,
    FAILURE,
    /** An approval the flow waited on was declined, so the flow stopped there. */
    DECLINED
}
