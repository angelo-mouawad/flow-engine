package be.ucll.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.ucll.model.FlowInstance;
import be.ucll.model.Request;
import be.ucll.model.RequestData;
import be.ucll.model.RequestStatus;
import be.ucll.model.RequestSubmission;
import be.ucll.repository.RequestSubmissionRepository;

@Service
public class RequestService {

    @Autowired
    private RequestSubmissionRepository requestSubmissionRepository;

    public List<RequestSubmission> getPendingRequests(){
        return requestSubmissionRepository.findByStatus(RequestStatus.PENDING);
    }
    
    public void processRequest(FlowInstance fi, Request request, Map<String, Object> data){
        RequestData rqData = new RequestData();
        data.forEach(rqData::setField);

        RequestSubmission submission = request.submit(rqData);
        submission.setFlowInstanceId(fi.getId());
        submission = requestSubmissionRepository.save(submission);
    }
}
