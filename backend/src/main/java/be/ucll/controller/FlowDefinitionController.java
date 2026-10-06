package be.ucll.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import be.ucll.controller.dto.FlowDefinitionInput;
import be.ucll.model.FlowDefinition;
import be.ucll.service.FlowDefinitionService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/flowDefinition")
public class FlowDefinitionController {

    private final FlowDefinitionService flowDefinitionService;

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
    @ResponseStatus(HttpStatus.CREATED)
    public FlowDefinition addFlowDefinition(@Valid @RequestBody FlowDefinitionInput input) {
        return flowDefinitionService.addFlowDefinition(input);
    }

    @PutMapping("/{id}")
    public FlowDefinition updateFlowDefinition(@PathVariable String id, @Valid @RequestBody FlowDefinitionInput input) {
        return flowDefinitionService.updateFlowDefinition(input, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlowDefinition(@PathVariable String id) {
        flowDefinitionService.deleteFlowDefinition(id);
        return ResponseEntity.noContent().build();
    }
}
