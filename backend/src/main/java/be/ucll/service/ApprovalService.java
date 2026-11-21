package be.ucll.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import be.ucll.exception.NotFoundException;
import be.ucll.exception.ServiceException;
import be.ucll.model.RequestSubmission;
import be.ucll.model.enums.RequestStatus;
import be.ucll.repository.RequestSubmissionRepository;

@Service
public class ApprovalService {

    private static final Logger log = LoggerFactory.getLogger(ApprovalService.class);

    /** How often a decision is retried when another approver saved the same submission in between. */
    static final int MAX_ATTEMPTS = 3;

    private final RequestSubmissionRepository requestSubmissionRepository;
    private final FlowRunnerService flowRunnerService;

    public ApprovalService(RequestSubmissionRepository requestSubmissionRepository, FlowRunnerService flowRunnerService) {
        this.requestSubmissionRepository = requestSubmissionRepository;
        this.flowRunnerService = flowRunnerService;
    }

    public RequestSubmission handleRequest(String requestId, RequestStatus decision, String user) {
        if (user == null || user.isBlank()) {
            throw new ServiceException("The UserEmail header cannot be empty");
        }

        RequestSubmission saved = saveDecision(requestId, decision, user);

        if (saved.isResolved() && saved.getFlowInstanceId() != null) {
            try {
                flowRunnerService.resumeFlow(saved.getFlowInstanceId());
            } catch (RuntimeException e) {
                // The decision is stored either way; the startup recovery picks up a flow left waiting here.
                log.warn("Request {} was {}, but resuming flow {} failed: {}",
                        requestId, saved.getStatus(), saved.getFlowInstanceId(), e.getMessage());
            }
        }
        return saved;
    }

    /**
     * Applies one vote. When another approver saved the same submission first, the version check
     * rejects our stale copy; we then reload and apply the vote again on top of theirs.
     */
    private RequestSubmission saveDecision(String requestId, RequestStatus decision, String user) {
        OptimisticLockingFailureException lastConflict = null;
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            RequestSubmission submission = requestSubmissionRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException("Request " + requestId + " does not exist"));
            submission.decide(user, decision);
            try {
                return requestSubmissionRepository.save(submission);
            } catch (OptimisticLockingFailureException conflict) {
                log.info("Request {} changed while {} was deciding, retrying ({}/{})", requestId, user, attempt, MAX_ATTEMPTS);
                lastConflict = conflict;
            }
        }
        throw lastConflict;
    }
}
