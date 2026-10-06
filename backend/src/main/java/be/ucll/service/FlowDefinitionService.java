package be.ucll.service;

import java.util.List;

import org.springframework.stereotype.Service;

import be.ucll.controller.dto.FlowDefinitionInput;
import be.ucll.exception.NotFoundException;
import be.ucll.model.FlowDefinition;
import be.ucll.repository.FlowDefinitionRepository;

@Service
public class FlowDefinitionService {

    private final FlowDefinitionRepository flowDefinitionRepository;

    public FlowDefinitionService(FlowDefinitionRepository flowDefinitionRepository) {
        this.flowDefinitionRepository = flowDefinitionRepository;
    }

    public List<FlowDefinition> findAllFlowDefinitions() {
        return flowDefinitionRepository.findAll();
    }

    public FlowDefinition findFlowDefinitionById(String id) {
        return flowDefinitionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Flow definition " + id + " does not exist"));
    }

    public FlowDefinition addFlowDefinition(FlowDefinitionInput input) {
        FlowDefinition definition = new FlowDefinition(
                input.title(),
                input.description(),
                input.triggerableBy(),
                input.processes());
        return flowDefinitionRepository.save(definition);
    }

    public FlowDefinition updateFlowDefinition(FlowDefinitionInput input, String id) {
        FlowDefinition definition = findFlowDefinitionById(id);
        definition.setTitle(input.title());
        definition.setDescription(input.description());
        definition.setTriggerableBy(input.triggerableBy());
        definition.setProcesses(input.processes());
        definition.touch();
        return flowDefinitionRepository.save(definition);
    }

    public void deleteFlowDefinition(String id) {
        flowDefinitionRepository.delete(findFlowDefinitionById(id));
    }
}
