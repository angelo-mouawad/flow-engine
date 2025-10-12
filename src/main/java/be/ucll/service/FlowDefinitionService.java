package be.ucll.service;

import be.ucll.model.FlowDefinition;
import be.ucll.repository.FlowDefinitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class FlowDefinitionService {
    private FlowDefinitionRepository flowDefinitionRepository;

    @Autowired
    public FlowDefinitionService(FlowDefinitionRepository flowDefinitionRepository) {
        this.flowDefinitionRepository = flowDefinitionRepository;
    }

    public List<FlowDefinition> findAllFlowDefinitions() {
        return flowDefinitionRepository.findAll();
    }

    public FlowDefinition addFlowDefinition(FlowDefinition flowDefinition) {
        
        return flowDefinitionRepository.save(
                new FlowDefinition(
                    flowDefinition.getTitle(), 
                    flowDefinition.getDescription(), 
                    flowDefinition.getProcesses()
                ));
    }
}
