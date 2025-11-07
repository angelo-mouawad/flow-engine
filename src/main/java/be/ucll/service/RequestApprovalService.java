package be.ucll.service;

import java.time.LocalDate;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.ucll.exception.ServiceException;
import be.ucll.model.RequestStatus;
import be.ucll.model.RequestSubmission;
import be.ucll.repository.RequestSubmissionRepository;

@Service
public class RequestApprovalService {

    @Autowired
    private RequestSubmissionRepository requestSubmissionRepository;

    @Autowired
    private FlowRunnerService flowRunnerService;

    public List<RequestSubmission> getPendingRequests(){
        return requestSubmissionRepository.findByStatus(RequestStatus.PENDING);
    }

    public void approveRequest(String requestId) {
        RequestSubmission submission = requestSubmissionRepository.findById(new ObjectId(requestId))
            .orElseThrow(() -> new ServiceException("Request not found"));

        if (submission.getStatus() != RequestStatus.PENDING) {
            throw new ServiceException("Request is not in pending state");
        }

        submission.setStatus(RequestStatus.APPROVED);
        submission.setProcessedAt(LocalDate.now());
        submission = requestSubmissionRepository.save(submission);

        flowRunnerService.resumeFlow(new ObjectId(submission.getFlowInstanceId()));
    }

    public void declineRequest(String requestId) {
        RequestSubmission submission = requestSubmissionRepository.findById(new ObjectId(requestId))
            .orElseThrow(() -> new ServiceException("Request not found"));

        if (submission.getStatus() != RequestStatus.PENDING) {
            throw new ServiceException("Request is not in pending state");
        }

        submission.setStatus(RequestStatus.DECLINED);
        submission.setProcessedAt(LocalDate.now());
        requestSubmissionRepository.save(submission);

        flowRunnerService.resumeFlow(new ObjectId(submission.getFlowInstanceId()));
    }
}
