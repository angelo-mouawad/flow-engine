package be.ucll.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.ucll.exception.ServiceException;
import be.ucll.model.Request;
import be.ucll.model.RequestData;
import be.ucll.model.enums.RequestStatus;
import be.ucll.model.RequestSubmission;
import be.ucll.repository.RequestSubmissionRepository;

@Service
public class RequestService {

    private RequestSubmissionRepository requestSubmissionRepository;

    @Autowired
    public RequestService(RequestSubmissionRepository requestSubmissionRepository){
        this.requestSubmissionRepository = requestSubmissionRepository;
    }

    public RequestSubmission getRequestById(String user, String id){

        RequestSubmission request = requestSubmissionRepository.findById(id).orElseThrow(
                () -> new ServiceException("request doesn't exist"));

        if (request.getStatus() != RequestStatus.PENDING){
            throw new ServiceException("request is not pending");
        }

        if(!request.isPendingForUser(user)){
            throw new ServiceException("request is not pending for " + user.replace('_', '.'));
        }

        return request;
    }

    public List<RequestSubmission> getPendingRequests(String user){

        List<RequestSubmission> requests = requestSubmissionRepository.findByStatus(RequestStatus.PENDING);
        requests.removeIf(r -> !r.isPendingForUser(user));

        return requests;
    }
    
    public void processRequest(Request request, Map<String, Object> data){
        RequestData rqData = new RequestData(data);

        RequestSubmission submission = request.submit(rqData);
        requestSubmissionRepository.save(submission);
    }
}
