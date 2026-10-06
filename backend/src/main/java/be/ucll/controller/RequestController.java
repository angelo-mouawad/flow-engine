package be.ucll.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import be.ucll.model.RequestSubmission;
import be.ucll.model.enums.RequestStatus;
import be.ucll.service.ApprovalService;
import be.ucll.service.RequestService;

@RestController
@RequestMapping("/request")
public class RequestController {

    private static final String USER_HEADER = "UserEmail";

    private final RequestService requestService;
    private final ApprovalService approvalService;

    public RequestController(RequestService requestService, ApprovalService approvalService) {
        this.requestService = requestService;
        this.approvalService = approvalService;
    }

    @GetMapping("/pending")
    public List<RequestSubmission> getPendingRequests(@RequestHeader(USER_HEADER) String user) {
        return requestService.getPendingRequests(user);
    }

    @GetMapping("/pending/{id}")
    public RequestSubmission getPendingRequestById(@RequestHeader(USER_HEADER) String user, @PathVariable String id) {
        return requestService.getPendingRequest(user, id);
    }

    @PutMapping("/approve/{id}")
    public RequestSubmission approveRequest(@PathVariable String id, @RequestHeader(USER_HEADER) String user) {
        return approvalService.handleRequest(id, RequestStatus.APPROVED, user);
    }

    @PutMapping("/decline/{id}")
    public RequestSubmission declineRequest(@PathVariable String id, @RequestHeader(USER_HEADER) String user) {
        return approvalService.handleRequest(id, RequestStatus.DECLINED, user);
    }
}
