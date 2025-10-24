package be.ucll.controller;

import be.ucll.model.FlowDefinition;
import be.ucll.model.FlowInstance;
import be.ucll.service.FlowInstanceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/flowInstance")
public class FlowInstanceController {

    private final FlowInstanceService flowInstanceService;

    public FlowInstanceController(FlowInstanceService flowInstanceService) {
        this.flowInstanceService = flowInstanceService;
    }

    @GetMapping
    public List<FlowInstance> findAllFlowInstances() {
        return flowInstanceService.findAllFlowInstances();
    }
}
