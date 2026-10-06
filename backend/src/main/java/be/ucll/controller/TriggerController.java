package be.ucll.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import be.ucll.controller.dto.FlowData;
import be.ucll.controller.dto.TriggerResponse;
import be.ucll.service.FlowRunnerService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/trigger")
public class TriggerController {

    private final FlowRunnerService flowRunnerService;

    public TriggerController(FlowRunnerService flowRunnerService) {
        this.flowRunnerService = flowRunnerService;
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public TriggerResponse triggerFlow(@PathVariable String id, @Valid @RequestBody FlowData flowData) {
        return flowRunnerService.instantiateFlow(id, flowData);
    }
}
