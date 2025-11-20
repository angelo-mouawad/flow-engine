package be.ucll.service;

import be.ucll.exception.ServiceException;
import be.ucll.model.FlowDefinition;
import be.ucll.model.FlowInstance;
import be.ucll.model.FlowStatus;
import be.ucll.model.Notification;
import be.ucll.model.Process;
import be.ucll.model.Request;
import be.ucll.repository.FlowDefinitionRepository;
import be.ucll.repository.FlowInstanceRepository;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
public class FlowRunnerService {

    @Autowired
    private FlowInstanceRepository flowInstanceRepository;

    @Autowired
    private FlowDefinitionRepository flowDefinitionRepository;

    @Autowired
    private RequestService requestService;

    public void instantiateFlow(String title, Map<String, Object> data){

        FlowDefinition fd =  flowDefinitionRepository.findByTitle(title)
            .orElseThrow(()-> new ServiceException("Flow with title "+ title +" does not exist"));

        FlowInstance flowInstance = new FlowInstance(fd, fd.getTitle());
        flowInstance = flowInstanceRepository.save(flowInstance);
        runFlow(flowInstance, data);
    }

    private void runFlow(FlowInstance flowInstance, Map<String, Object> data){
        try{

            while (flowInstance.getCurrentProcess() != null) {
                Process current = flowInstance.getCurrentProcess();

                if(current instanceof Request){
                    requestService.processRequest(flowInstance,  (Request) current, data);
                    pauseFlow(flowInstance);
                    return;
                }else if (current instanceof Notification) {
                    // Notify in NotificationService
                }

                flowInstance.nextProcess();
                flowInstance.setUpdatedAt(LocalDate.now());
                flowInstance = flowInstanceRepository.save(flowInstance);
            }

            flowInstance.setFlowStatus(FlowStatus.SUCCESS);
            flowInstanceRepository.save(flowInstance);

        } catch(Exception e) {
            flowInstance.setFlowStatus(FlowStatus.FAILURE);
            flowInstance.setUpdatedAt(LocalDate.now());
            flowInstanceRepository.save(flowInstance);
            throw new ServiceException("Flow Execution Failed: "+ e.getMessage());
        }
    }

    public void pauseFlow(FlowInstance flowInstance){
        flowInstance.setFlowStatus(FlowStatus.PENDING);
        flowInstance.setUpdatedAt(LocalDate.now());
        flowInstanceRepository.save(flowInstance);
    }

    public void resumeFlow(ObjectId id){
        FlowInstance flowInstance = flowInstanceRepository.findById(id)
            .orElseThrow(()-> new ServiceException("Flow Instance not found"));

        if(flowInstance.getFlowStatus() != FlowStatus.PENDING){
            throw new ServiceException("Flow is not PENDING");
        }

        flowInstance.nextProcess();
        flowInstance.setFlowStatus(FlowStatus.ACTIVE);
        flowInstance.setUpdatedAt(LocalDate.now());
        flowInstance = flowInstanceRepository.save(flowInstance);
        runFlow(flowInstance, Map.of());
    }
}
