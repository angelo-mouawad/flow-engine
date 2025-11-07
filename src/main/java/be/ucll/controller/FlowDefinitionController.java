package be.ucll.controller;

import be.ucll.controller.dto.FlowDefinitionInput;
import be.ucll.model.FlowDefinition;
import be.ucll.service.FlowDefinitionService;
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
@CrossOrigin(origins = "*")
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

    @PostMapping
    public FlowDefinition addFlowDefinition(@RequestBody FlowDefinitionInput input) {
        return flowDefinitionService.addFlowDefinition(input);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleDomainException(RuntimeException ex, WebRequest request) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("Error: ", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }
}
