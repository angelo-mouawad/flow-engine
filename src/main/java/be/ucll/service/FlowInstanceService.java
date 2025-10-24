package be.ucll.service;

import be.ucll.model.FlowDefinition;
import be.ucll.model.FlowInstance;
import be.ucll.repository.FlowInstanceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlowInstanceService {

    FlowInstanceRepository flowInstanceRepository;

    public FlowInstanceService(FlowInstanceRepository flowInstanceRepository) {
        this.flowInstanceRepository = flowInstanceRepository;
    }

    public List<FlowInstance> findAllFlowInstances() {
        return flowInstanceRepository.findAll();
    }
}
