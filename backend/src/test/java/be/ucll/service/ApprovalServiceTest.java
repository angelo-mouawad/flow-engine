package be.ucll.service;

import static be.ucll.model.TestProcesses.approvableAbsence;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import be.ucll.exception.DomainException;
import be.ucll.exception.NotFoundException;
import be.ucll.exception.ServiceException;
import be.ucll.model.RequestData;
import be.ucll.model.RequestSubmission;
import be.ucll.model.enums.RequestStatus;
import be.ucll.repository.RequestSubmissionRepository;

@ExtendWith(MockitoExtension.class)
class ApprovalServiceTest {

    @Mock
    private RequestSubmissionRepository repository;
    @Mock
    private FlowRunnerService flowRunnerService;

    private ApprovalService approvalService;

    @BeforeEach
    void setUp() {
        approvalService = new ApprovalService(repository, flowRunnerService);
    }

    private RequestSubmission pending(int minApprovals) {
        return new RequestSubmission(new RequestData(), approvableAbsence(minApprovals), "flow-1", RequestStatus.PENDING);
    }

    @Test
    void resumesTheFlowWhenARequestIsResolved() {
        when(repository.findById("req")).thenReturn(Optional.of(pending(1)));
        when(repository.save(any(RequestSubmission.class))).thenAnswer(call -> call.getArgument(0));

        RequestSubmission result = approvalService.handleRequest("req", RequestStatus.APPROVED, "adam@glackit.be");

        assertEquals(RequestStatus.APPROVED, result.getStatus());
        verify(flowRunnerService).resumeFlow("flow-1");
    }

    @Test
    void doesNotResumeWhileMoreApprovalsAreNeeded() {
        when(repository.findById("req")).thenReturn(Optional.of(pending(2)));
        when(repository.save(any(RequestSubmission.class))).thenAnswer(call -> call.getArgument(0));

        approvalService.handleRequest("req", RequestStatus.APPROVED, "adam@glackit.be");

        verify(flowRunnerService, never()).resumeFlow(any());
    }

    @Test
    void aFailingResumeDoesNotUndoTheApproval() {
        when(repository.findById("req")).thenReturn(Optional.of(pending(1)));
        when(repository.save(any(RequestSubmission.class))).thenAnswer(call -> call.getArgument(0));
        doThrow(new NotFoundException("Flow instance flow-1 does not exist")).when(flowRunnerService).resumeFlow("flow-1");

        RequestSubmission result = approvalService.handleRequest("req", RequestStatus.APPROVED, "adam@glackit.be");

        assertEquals(RequestStatus.APPROVED, result.getStatus());
    }

    @Test
    void unknownRequestIsNotFound() {
        when(repository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> approvalService.handleRequest("missing", RequestStatus.APPROVED, "adam@glackit.be"));
    }

    @Test
    void outsiderCannotApprove() {
        when(repository.findById("req")).thenReturn(Optional.of(pending(1)));

        assertThrows(DomainException.class,
                () -> approvalService.handleRequest("req", RequestStatus.APPROVED, "lais@glackit.be"));
        verify(repository, never()).save(any());
    }

    @Test
    void blankUserIsRejected() {
        assertThrows(ServiceException.class, () -> approvalService.handleRequest("req", RequestStatus.APPROVED, " "));
    }
}
