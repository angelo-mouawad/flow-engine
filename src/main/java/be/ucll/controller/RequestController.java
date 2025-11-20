package be.ucll.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import be.ucll.model.RequestStatus;
import be.ucll.model.RequestSubmission;
import be.ucll.service.ApprovalService;
import be.ucll.service.RequestService;

@RestController
@RequestMapping("/request")
@CrossOrigin(origins = "*")
public class RequestController {

    @Autowired
    private RequestService requestService;

    @Autowired
    private ApprovalService approvalService;

    @GetMapping("/pending")
    public List<RequestSubmission> getPendingRequests(){
        return requestService.getPendingRequests();        
    }

    @PutMapping("/approve/{id}")
    public void approveRequest(@PathVariable String id){
        approvalService.approveRequest(id, RequestStatus.APPROVED);
    }

    @PutMapping("/decline/{id}")
    public void declineRequest(@PathVariable String id){
        approvalService.approveRequest(id, RequestStatus.DECLINED);
    }
}
