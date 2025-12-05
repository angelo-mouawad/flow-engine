package be.ucll.controller;

import be.ucll.model.FlowInstance;
import be.ucll.service.FlowDefinitionService;
import be.ucll.service.FlowInstanceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/flowInstance")
public class FlowInstanceController {

    private final FlowInstanceService flowInstanceService;

    @Autowired
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

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleDomainException(RuntimeException ex, WebRequest request) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("Error: ", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }
}
