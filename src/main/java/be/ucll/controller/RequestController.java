package be.ucll.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import be.ucll.model.RequestSubmission;
import be.ucll.service.RequestApprovalService;

@RestController
@RequestMapping("/request")
public class RequestController {

    @Autowired
    private RequestApprovalService requestApprovalService;

    @GetMapping("/pending")
    public List<RequestSubmission> getPendingRequests(){
        return requestApprovalService.getPendingRequests();        
    }

    @PutMapping("/approve/{id}")
    public void approveRequest(@PathVariable String id){
        requestApprovalService.approveRequest(id);
    }

    @PutMapping("/decline/{id}")
    public void declineRequest(@PathVariable String id){
        requestApprovalService.declineRequest(id);
    }
}
