package be.ucll.service;

import be.ucll.exception.ServiceException;
import be.ucll.model.*;
import be.ucll.model.Process;
import be.ucll.model.enums.FlowStatus;
import be.ucll.repository.FlowDefinitionRepository;
import be.ucll.repository.FlowInstanceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class FlowRunnerService {

    @Autowired
    private FlowInstanceRepository flowInstanceRepository;

    @Autowired
    private FlowDefinitionRepository flowDefinitionRepository;

    @Autowired
    private RequestService requestService;

    public void instantiateFlow(String id, Map<String, Map<String, Object>> data){

        FlowDefinition fd =  flowDefinitionRepository.findById(id)
            .orElseThrow(()-> new ServiceException("Flow id does not exist"));

        FlowInstance flowInstance = new FlowInstance(fd, fd.getTitle(), data);
        flowInstance = flowInstanceRepository.save(flowInstance);
        runFlow(flowInstance);
    }

    private void runFlow(FlowInstance flowInstance){
        try{
            Map<String, Map<String, Object>> data = flowInstance.getData();

            while (flowInstance.getCurrentProcess() != null) {
                Process current = flowInstance.getCurrentProcess();
                current.setFlowInstanceId(flowInstance.getId());

                switch (current) {
                    case Request request -> {
                        requestService.processRequest(
                                request,
                                data.get(request.getRequestTypeName())); 
                    }
                    case Approval approval -> {
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

            flowInstance.setFlowStatus(FlowStatus.SUCCESS);
            flowInstanceRepository.save(flowInstance);

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
