package be.ucll.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import be.ucll.model.FlowInstance;
import be.ucll.service.FlowInstanceService;

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

    @GetMapping("/{id}")
    public FlowInstance findFlowInstanceById(@PathVariable String id) {
        return flowInstanceService.findFlowInstanceById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlowInstanceById(@PathVariable String id) {
        flowInstanceService.deleteFlowInstanceById(id);
        return ResponseEntity.noContent().build();
    }
}
