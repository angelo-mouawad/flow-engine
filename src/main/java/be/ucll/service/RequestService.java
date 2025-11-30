package be.ucll.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
