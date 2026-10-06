package be.ucll.service;

import java.util.List;

import org.springframework.stereotype.Service;

import be.ucll.exception.NotFoundException;
import be.ucll.model.FlowInstance;
import be.ucll.repository.FlowInstanceRepository;

@Service
public class FlowInstanceService {

    private final FlowInstanceRepository flowInstanceRepository;
    private final RequestService requestService;

    public FlowInstanceService(FlowInstanceRepository flowInstanceRepository, RequestService requestService) {
        this.flowInstanceRepository = flowInstanceRepository;
        this.requestService = requestService;
    }

    public List<FlowInstance> findAllFlowInstances() {
        return flowInstanceRepository.findAll();
    }

    public FlowInstance findFlowInstanceById(String id) {
        return flowInstanceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Flow instance " + id + " does not exist"));
    }

    public void deleteFlowInstanceById(String id) {
        if (!flowInstanceRepository.existsById(id)) {
            throw new NotFoundException("Flow instance " + id + " does not exist");
        }
        requestService.deleteForFlowInstance(id);
        flowInstanceRepository.deleteById(id);
    }
}
