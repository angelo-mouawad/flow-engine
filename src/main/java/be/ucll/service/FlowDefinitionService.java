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

    public FlowDefinition addFlowDefinition(FlowDefinitionInput input) {

        List<Process> processes = new ArrayList<>();

        for (String processId : input.processes()) {
            Process p = processRepository.findById(new ObjectId(processId)).orElseThrow(() -> new ServiceException("No process found by id " + processId));
            processes.add(p);
        }

        FlowDefinition flow = new FlowDefinition(
                input.title(),
                input.description(),
                processes
        );

        return flowDefinitionRepository.save(flow);
    }
}
