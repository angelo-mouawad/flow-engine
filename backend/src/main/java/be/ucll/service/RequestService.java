package be.ucll.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import be.ucll.exception.DomainException;
import be.ucll.exception.NotFoundException;
import be.ucll.exception.ServiceException;
import be.ucll.model.Request;
import be.ucll.model.RequestData;
import be.ucll.model.RequestSubmission;
import be.ucll.model.enums.RequestStatus;
import be.ucll.repository.RequestSubmissionRepository;

@Service
public class RequestService {

    private final RequestSubmissionRepository requestSubmissionRepository;

    public RequestService(RequestSubmissionRepository requestSubmissionRepository) {
        this.requestSubmissionRepository = requestSubmissionRepository;
    }

    public RequestSubmission getPendingRequest(String user, String id) {
        requireUser(user);
        RequestSubmission submission = requestSubmissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Request " + id + " does not exist"));

        if (!submission.isPendingFor(user)) {
            throw new NotFoundException("Request " + id + " is not waiting on " + user.trim());
        }
        return submission;
    }

    public List<RequestSubmission> getPendingRequests(String user) {
        requireUser(user);
        return requestSubmissionRepository.findByStatus(RequestStatus.PENDING).stream()
                .filter(submission -> submission.isPendingFor(user))
                .toList();
    }

    public RequestSubmission processRequest(Request request, Map<String, Object> data, String flowInstanceId) {
        if (data == null) {
            throw new DomainException("No data was sent for " + request.getRequestTypeName()
                    + ", which request " + request.getLabel() + " needs");
        }
        RequestSubmission submission = request.submit(new RequestData(data), flowInstanceId);
        return requestSubmissionRepository.save(submission);
    }

    public boolean hasPendingSubmission(String flowInstanceId, Collection<Integer> requestSteps) {
        return requestSubmissionRepository.findByFlowInstanceId(flowInstanceId).stream()
                .anyMatch(submission -> submission.getStatus() == RequestStatus.PENDING
                        && requestSteps.contains(submission.getRequestStep()));
    }

    public void deleteForFlowInstance(String flowInstanceId) {
        requestSubmissionRepository.deleteByFlowInstanceId(flowInstanceId);
    }

    private static void requireUser(String user) {
        if (user == null || user.isBlank()) {
            throw new ServiceException("The UserEmail header cannot be empty");
        }
    }
}
