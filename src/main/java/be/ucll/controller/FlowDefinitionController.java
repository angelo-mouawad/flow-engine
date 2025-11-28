package be.ucll.controller;

import be.ucll.controller.dto.FlowDefinitionInput;
import be.ucll.model.FlowDefinition;
import be.ucll.service.FlowDefinitionService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/{id}")
    public FlowDefinition findFlowDefinitionById(@PathVariable String id) {
        return flowDefinitionService.findFlowDefinitionById(id);
    }

    @PostMapping
    public FlowDefinition addFlowDefinition(@RequestBody @Valid FlowDefinitionInput input) {
        return flowDefinitionService.addFlowDefinition(input);
    }

    @PutMapping("/{id}")
    public FlowDefinition updateFlowDefinition(@RequestBody FlowDefinitionInput updatedFlowDefinition, @PathVariable String id) {
        return flowDefinitionService.updateFlowDefinition(updatedFlowDefinition, id);
    }

    @DeleteMapping("/{id}")
    public void deleteFlowDefinition(@PathVariable String id) {
        flowDefinitionService.deleteFlowDefinition(id);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleDomainException(RuntimeException ex, WebRequest request) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("Error: ", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }
}
