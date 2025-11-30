package be.ucll.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import be.ucll.service.FlowRunnerService;

@RestController
@RequestMapping("/trigger")
public class TriggerController {
    
    private FlowRunnerService flowRunnerService;

    @Autowired
    public TriggerController(FlowRunnerService flowRunnerService){
        this.flowRunnerService = flowRunnerService;
    }

    @PostMapping("/{id}")
    public void triggerFlow(@PathVariable String id, @RequestBody Map<String, Map<String, Object>> data){
        flowRunnerService.instantiateFlow(id, data); 
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleDomainException(RuntimeException ex, WebRequest request) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("Error: ", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }
}
