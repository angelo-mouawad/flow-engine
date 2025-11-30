package be.ucll.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.ucll.exception.ServiceException;
import be.ucll.model.enums.RequestStatus;
import be.ucll.model.RequestSubmission;
import be.ucll.repository.RequestSubmissionRepository;

@Service
public class ApprovalService {
    
    private FlowRunnerService flowRunnerService;
    private RequestSubmissionRepository requestSubmissionRepository;

    @Autowired
    public ApprovalService(RequestSubmissionRepository requestSubmissionRepository, FlowRunnerService flowRunnerService){
        this.requestSubmissionRepository = requestSubmissionRepository;
        this.flowRunnerService = flowRunnerService;
    }

    public void approveRequest(String requestId, RequestStatus status, String user) {
        RequestSubmission submission = requestSubmissionRepository.findById(requestId)
            .orElseThrow(() -> new ServiceException("Request not found"));

        if (submission.getStatus() != RequestStatus.PENDING) {
            throw new ServiceException("Request is not in pending state");
        }

        try {
            submission.approve(user, status);
            requestSubmissionRepository.save(submission);
            if (submission.getStatus() == RequestStatus.APPROVED || 
                 submission.getStatus() == RequestStatus.DECLINED) {
                flowRunnerService.resumeFlow(submission.getFlowInstanceId());
            }
        } catch (RuntimeException e) {
            throw new ServiceException(e.getMessage());
        }
    }
}
