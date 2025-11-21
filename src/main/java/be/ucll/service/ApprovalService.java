package be.ucll.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.ucll.exception.ServiceException;
import be.ucll.model.RequestStatus;
import be.ucll.model.RequestSubmission;
import be.ucll.repository.RequestSubmissionRepository;

@Service
public class ApprovalService {
    
    @Autowired
    private FlowRunnerService flowRunnerService;

    @Autowired
    private RequestSubmissionRepository requestSubmissionRepository;

    public void approveRequest(String requestId, RequestStatus status) {
        RequestSubmission submission = requestSubmissionRepository.findById(requestId)
            .orElseThrow(() -> new ServiceException("Request not found"));

        if (submission.getStatus() != RequestStatus.PENDING) {
            throw new ServiceException("Request is not in pending state");
        }

        submission.setStatus(status);
        submission.setProcessedAt(LocalDate.now());
        submission = requestSubmissionRepository.save(submission);

        flowRunnerService.resumeFlow(submission.getFlowInstanceId());
    }
}
