package be.ucll.service;

import be.ucll.exception.ServiceException;
import be.ucll.model.FlowDefinition;
import be.ucll.model.FlowInstance;
import be.ucll.model.FlowStatus;
import be.ucll.model.Process;
import be.ucll.model.Request;
import be.ucll.model.Trigger;
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
    private RequestProcessingService requestProcessingService;

    public void instantiateFlow(String title, Map<String, Object> data){

        FlowDefinition fd =  flowDefinitionRepository.findByTitle(title)
            .orElseThrow(()-> new ServiceException("Flow with title "+ title +" does not exist"));

        Trigger trigger = fd.getTrigger();
        if (trigger == Trigger.POST || trigger == Trigger.ALL){
            FlowInstance fi = new FlowInstance(fd, fd.getTitle());
            fi = flowInstanceRepository.save(fi);
            runFlow(fi, data);
        }else{
            throw new ServiceException("Flow Definition "+ fd.getTitle() +" cannot be triggered By Post request");
        }
    }

    private void runFlow(FlowInstance fi, Map<String, Object> data){
        try{

            while (fi.getCurrentProcess() != null) {
                Process current = fi.getCurrentProcess();
                
                if(current instanceof Request){
                    requestProcessingService.processRequest(fi,  (Request) current, data);

                    fi.setFlowStatus(FlowStatus.PENDING);
                    fi.setUpdatedAt(LocalDate.now());
                    flowInstanceRepository.save(fi);
                    return;
                }

                current.execute();

                fi.nextProcess();
                fi.setUpdatedAt(LocalDate.now());
                fi = flowInstanceRepository.save(fi);
            }

            fi.setFlowStatus(FlowStatus.SUCCESS);
            flowInstanceRepository.save(fi);

        } catch(Exception e) {
            fi.setFlowStatus(FlowStatus.FAILURE);
            fi.setUpdatedAt(LocalDate.now());
            flowInstanceRepository.save(fi);
            throw new ServiceException("Flow Execution Failed: "+ e.getMessage());
        }
    }

    public void resumeFlow(ObjectId id){
        FlowInstance fi = flowInstanceRepository.findById(id)
            .orElseThrow(()-> new ServiceException("Flow Instance not found"));

        if(fi.getFlowStatus() != FlowStatus.PENDING){
            throw new ServiceException("Flow is not PENDING");
        }

        fi.nextProcess();
        fi.setFlowStatus(FlowStatus.ACTIVE);
        fi.setUpdatedAt(LocalDate.now());
        fi = flowInstanceRepository.save(fi);
        runFlow(fi, Map.of());
    }
}
