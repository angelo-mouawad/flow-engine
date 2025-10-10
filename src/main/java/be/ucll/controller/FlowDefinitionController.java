package be.ucll.controller;

import be.ucll.model.FlowDefinition;
import be.ucll.service.FlowDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/flowDefinition")
public class FlowDefinitionController {
    private FlowDefinitionService flowDefinitionService;

    @Autowired
    public FlowDefinitionController(FlowDefinitionService flowDefinitionService) {
        this.flowDefinitionService = flowDefinitionService;
    }

    @GetMapping
    public List<FlowDefinition> findAllFlowDefinitions() {
        return flowDefinitionService.findAllFlowDefinitions();
    }
}