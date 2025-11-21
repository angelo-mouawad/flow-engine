package be.ucll.model;

public class Approval extends Process{

    private RequestSubmission requestSubmission;

    public Approval(String name){
        super(name, ProcessType.APPROVAL);
    }

    public RequestSubmission getRequestSubmission() {
        return requestSubmission;
    }

    public void setRequestSubmission(RequestSubmission requestSubmission) {
        this.requestSubmission = requestSubmission;
    }
}
