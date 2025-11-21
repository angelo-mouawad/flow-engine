package be.ucll.service;

import be.ucll.controller.dto.FlowDefinitionInput;
import be.ucll.exception.DomainException;
import be.ucll.exception.ServiceException;
import be.ucll.model.FlowDefinition;
import be.ucll.model.Process;
import be.ucll.repository.FlowDefinitionRepository;
import be.ucll.repository.ProcessRepository;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Service
public class FlowDefinitionService {

    @Autowired
    private FlowDefinitionRepository flowDefinitionRepository;

    @Autowired
    private ProcessRepository processRepository;

    public List<FlowDefinition> findAllFlowDefinitions() {
        return flowDefinitionRepository.findAll();
    }

    public List<Process> getProcessesByIds(List<String> input) {
        List<Process> processes = new ArrayList<>();

        for (String processId : input) {
            Process p = processRepository.findById(new ObjectId(processId)).orElseThrow(() -> new ServiceException("No process found by id " + processId));
            processes.add(p);
        }

        return processes;
    }

    public FlowDefinition addFlowDefinition(FlowDefinitionInput input) {

        FlowDefinition flow = new FlowDefinition(
                input.title(),
                input.description(),
                getProcessesByIds(input.processes())
        );

        return flowDefinitionRepository.save(flow);
    }

    public FlowDefinition updateFlowDefinition(FlowDefinitionInput updatedFlowDefintion, ObjectId id) {
        FlowDefinition oldFlowDefinition = flowDefinitionRepository.findById(id).orElseThrow(() -> new ServiceException("No id found"));

        oldFlowDefinition.setTitle(updatedFlowDefintion.title());
        oldFlowDefinition.setDescription(updatedFlowDefintion.description());
        oldFlowDefinition.setProcesses(getProcessesByIds(updatedFlowDefintion.processes()));


        return flowDefinitionRepository.save(oldFlowDefinition);
    }
}
