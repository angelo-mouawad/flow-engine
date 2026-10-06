package be.ucll.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import be.ucll.exception.NotFoundException;
import be.ucll.exception.ServiceException;
import be.ucll.model.RequestSubmission;
import be.ucll.model.enums.RequestStatus;
import be.ucll.repository.RequestSubmissionRepository;

@Service
public class ApprovalService {

    private static final Logger log = LoggerFactory.getLogger(ApprovalService.class);

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

        RequestSubmission submission = requestSubmissionRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request " + requestId + " does not exist"));

        submission.decide(user, decision);
        RequestSubmission saved = requestSubmissionRepository.save(submission);

        if (saved.isResolved() && saved.getFlowInstanceId() != null) {
            try {
                flowRunnerService.resumeFlow(saved.getFlowInstanceId());
            } catch (RuntimeException e) {
                log.warn("Request {} was {}, but resuming flow {} failed: {}",
                        requestId, saved.getStatus(), saved.getFlowInstanceId(), e.getMessage());
            }
        }
        return saved;
    }
}
