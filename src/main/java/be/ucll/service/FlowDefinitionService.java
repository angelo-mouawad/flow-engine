package be.ucll.service;

import be.ucll.controller.dto.FlowDefinitionInput;
import be.ucll.model.FlowDefinition;
import be.ucll.repository.FlowDefinitionRepository;
import be.ucll.repository.FlowInstanceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlowDefinitionService {
    @Autowired
    private FlowDefinitionRepository flowDefinitionRepository;

    public List<FlowDefinition> findAllFlowDefinitions() {
        return flowDefinitionRepository.findAll();
    }

    public FlowDefinition addFlowDefinition(FlowDefinitionInput input) {

        FlowDefinition fd = new FlowDefinition(
                input.title(), 
                input.description(),
                input.processes(),
                input.trigger());

        return flowDefinitionRepository.save(fd);
    }
}
