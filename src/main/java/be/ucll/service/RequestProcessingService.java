package be.ucll.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.ucll.model.FlowInstance;
import be.ucll.model.Request;
import be.ucll.model.RequestData;
import be.ucll.model.RequestSubmission;
import be.ucll.repository.RequestSubmissionRepository;

@Service
public class RequestProcessingService {

    @Autowired
    private RequestSubmissionRepository requestSubmissionRepository;
    
    public void processRequest(FlowInstance fi, Request request, Map<String, Object> data){
        RequestData rqData = new RequestData();
        data.forEach(rqData::setField);
        request.setData(rqData);

        RequestSubmission submission = request.getRequestType().process(rqData);
        submission.setFlowInstanceId(fi.getId());
        submission = requestSubmissionRepository.save(submission);

        request.setSubmission(submission);
    }
}
