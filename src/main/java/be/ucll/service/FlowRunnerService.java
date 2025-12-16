package be.ucll.service;

import be.ucll.exception.ServiceException;
import be.ucll.model.*;
import be.ucll.model.Process;
import be.ucll.model.enums.FlowStatus;
import be.ucll.model.enums.RequestStatus;
import be.ucll.model.enums.RequestTypeEnum;
import be.ucll.repository.FlowDefinitionRepository;
import be.ucll.repository.FlowInstanceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.ucll.controller.dto.FlowData;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Service
public class FlowRunnerService {

    @Autowired
    private FlowInstanceRepository flowInstanceRepository;

    @Autowired
    private FlowDefinitionRepository flowDefinitionRepository;

    @Autowired
    private RequestService requestService;

    public void instantiateFlow(String id, String url, FlowData flowData){

        FlowDefinition fd =  flowDefinitionRepository.findById(id)
            .orElseThrow(()-> new ServiceException("Flow id does not exist"));

        if (!fd.isAnyTrigger() && !fd.getTriggerableBy().contains(flowData.triggeredBy())) {
            throw new ServiceException("Flow is not triggerable by " + flowData.triggeredBy());
        }

        FlowInstance flowInstance = new FlowInstance(fd, 
                flowData.title(), 
                flowData.triggeredBy(), 
                flowData.data(),
                url);

        flowInstance = flowInstanceRepository.save(flowInstance);
        runFlow(flowInstance);
    }

    private void runFlow(FlowInstance flowInstance){
        try{
            Map<RequestTypeEnum, Map<String, Object>> data = flowInstance.getData();

            while (flowInstance.getCurrentProcess() != null) {
                Process current = flowInstance.getCurrentProcess();
                current.setFlowInstanceId(flowInstance.getId());

                switch (current) {
                    case Request request -> {
                        RequestSubmission submission = requestService.processRequest(
                                request,
                                data.get(request.getRequestTypeName())); 
                        flowInstance.addSubmission(submission);
                        break;
                    }
                    case Approval approval -> {

                        Set<Integer> requestSteps = approval.getRequestSteps();
                        boolean shouldWait = flowInstance.getSubmissions()
                            .stream()
                            .anyMatch(rq -> requestSteps.contains(rq.getRequestStep() ));

                        if (!shouldWait) break;

                        updateFlowStatus(flowInstance, FlowStatus.PENDING);
                        return;
                    }
                    case Notification notification -> {
                        //TODO: Notify in NotificationService
                    }
                    default -> { break; }
                }

                flowInstance.nextProcess();
                flowInstance.setUpdatedAt(LocalDateTime.now());
                flowInstanceRepository.save(flowInstance); 
            }

            updateFlowStatus(flowInstance, FlowStatus.SUCCESS);

            //TODO: Send FlowInstance back to Calling URL
        } catch(Exception e) {
            updateFlowStatus(flowInstance, FlowStatus.FAILURE);
            throw new ServiceException("Flow Execution Failed: "+ e.getMessage());
        }
    }

    public void resumeFlow(String id){
        FlowInstance flowInstance = flowInstanceRepository.findById(id)
            .orElseThrow(()-> new ServiceException("Flow Instance not found"));

        if(flowInstance.getFlowStatus() != FlowStatus.PENDING){
            throw new ServiceException("Flow is not PENDING");
        }

        // Update Submissions
        flowInstance.getSubmissions()
            .removeAll(
                    flowInstance.getSubmissions().stream()
                    .filter(rq -> rq.getStatus() != RequestStatus.PENDING)
                    .toList());

        flowInstance.nextProcess();
        flowInstance.setFlowStatus(FlowStatus.ACTIVE);
        flowInstance.setUpdatedAt(LocalDateTime.now());
        flowInstanceRepository.save(flowInstance); 

        runFlow(flowInstance);
    }

    private void updateFlowStatus(FlowInstance flowInstance, FlowStatus status){
        flowInstance.setFlowStatus(status);
        flowInstance.setUpdatedAt(LocalDateTime.now());
        flowInstanceRepository.save(flowInstance);
    }
}
