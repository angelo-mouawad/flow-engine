package be.ucll.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import be.ucll.model.enums.RequestStatus;
import be.ucll.model.RequestSubmission;
import be.ucll.service.ApprovalService;
import be.ucll.service.RequestService;

@RestController
@RequestMapping("/request")
public class RequestController {

    private RequestService requestService;
    private ApprovalService approvalService;

    @Autowired
    public RequestController(RequestService requestService, ApprovalService approvalService){
        this.approvalService = approvalService;
        this.requestService = requestService;
    }

    @GetMapping("/pending/{id}")
    public RequestSubmission getPendingRequestById(@RequestHeader("UserEmail") String user, @PathVariable String id){
        return requestService.getRequestById(user, id);        
    }

    @GetMapping("/pending")
    public List<RequestSubmission> getPendingRequests(@RequestHeader("UserEmail") String user){
        return requestService.getPendingRequests(user);        
    }

    @PutMapping("/approve/{id}")
    public void approveRequest(@PathVariable String id, @RequestHeader("UserEmail") String user){
        approvalService.approveRequest(id, RequestStatus.APPROVED, user);
    }

    @PutMapping("/decline/{id}")
    public void declineRequest(@PathVariable String id, @RequestHeader("UserEmail") String user){
        approvalService.approveRequest(id, RequestStatus.DECLINED, user);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleDomainException(RuntimeException ex, WebRequest request) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("Error: ", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }
}
