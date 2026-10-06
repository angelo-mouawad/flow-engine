package be.ucll.service;

import be.ucll.controller.dto.FlowDefinitionInput;
import be.ucll.exception.ServiceException;
import be.ucll.model.FlowDefinition;
import be.ucll.repository.FlowDefinitionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlowDefinitionService {

    private FlowDefinitionRepository flowDefinitionRepository;

    @Autowired
    public FlowDefinitionService(FlowDefinitionRepository flowDefinitionRepository){
        this.flowDefinitionRepository = flowDefinitionRepository;
    }

    public List<FlowDefinition> findAllFlowDefinitions() {
        return flowDefinitionRepository.findAll();
    }

    public FlowDefinition findFlowDefinitionById(String id) {
        return flowDefinitionRepository.findById(id)
                .orElseThrow(() -> new ServiceException("No id found"));
    }

    public FlowDefinition addFlowDefinition(FlowDefinitionInput input) {

        FlowDefinition flow = new FlowDefinition(
                input.title(),
                input.description(),
                input.triggerableBy(),
                input.processes()
        );

        return flowDefinitionRepository.save(flow);
    }

    public FlowDefinition updateFlowDefinition(FlowDefinitionInput input, String id) {
        FlowDefinition flowDefinition = flowDefinitionRepository.findById(id)
          .orElseThrow(() -> new ServiceException("No id found"));

        flowDefinition.setTitle(input.title());
        flowDefinition.setDescription(input.description());
        flowDefinition.setTriggerableBy(input.triggerableBy());
        flowDefinition.setProcesses(input.processes());

        return flowDefinitionRepository.save(flowDefinition);
    }

    public void deleteFlowDefinition(String id) {
        FlowDefinition updatedFlowDefinition = flowDefinitionRepository.findById(id)
                .orElseThrow(() -> new ServiceException("No id found"));;
        flowDefinitionRepository.delete(updatedFlowDefinition);
    }
}
