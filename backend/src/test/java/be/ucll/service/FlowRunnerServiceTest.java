package be.ucll.service;

import static be.ucll.model.TestProcesses.approvableAbsence;
import static be.ucll.model.TestProcesses.approvalOf;
import static be.ucll.model.TestProcesses.plainAbsence;
import static be.ucll.model.TestProcesses.popupFor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import be.ucll.controller.dto.FlowData;
import be.ucll.controller.dto.TriggerResponse;
import be.ucll.exception.DomainException;
import be.ucll.exception.NotFoundException;
import be.ucll.exception.ServiceException;
import be.ucll.model.FlowDefinition;
import be.ucll.model.FlowInstance;
import be.ucll.model.Notification;
import be.ucll.model.Process;
import be.ucll.model.Request;
import be.ucll.model.RequestData;
import be.ucll.model.RequestSubmission;
import be.ucll.model.enums.FlowStatus;
import be.ucll.model.enums.RequestStatus;
import be.ucll.model.enums.RequestTypeEnum;
import be.ucll.repository.FlowDefinitionRepository;
import be.ucll.repository.FlowInstanceRepository;

@ExtendWith(MockitoExtension.class)
class FlowRunnerServiceTest {

    private static final String CALLBACK = "http://listener.test/hook";
    private static final Map<RequestTypeEnum, Map<String, Object>> ABSENCE_DATA =
            Map.of(RequestTypeEnum.ABSENCE_REQUEST, Map.of("Reason", "sick"));

    @Mock
    private FlowInstanceRepository flowInstanceRepository;
    @Mock
    private FlowDefinitionRepository flowDefinitionRepository;
    @Mock
    private RequestService requestService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private CallbackService callbackService;

    private FlowRunnerService runner;

    @BeforeEach
    void setUp() {
        runner = new FlowRunnerService(flowInstanceRepository, flowDefinitionRepository,
                requestService, notificationService, callbackService);
    }

    private FlowDefinition definition(List<Process> processes) {
        return new FlowDefinition("Absence", "Report an absence", Set.of("lais@glackit.be"), processes);
    }

    private FlowData trigger(String user) {
        return new FlowData("Absence for Lais", user, ABSENCE_DATA, CALLBACK);
    }

    private void stubDefinition(FlowDefinition definition) {
        when(flowDefinitionRepository.findById("def")).thenReturn(Optional.of(definition));
    }

    private void saveReturnsArgument() {
        when(flowInstanceRepository.save(any(FlowInstance.class))).thenAnswer(call -> call.getArgument(0));
    }

    private RequestSubmission submission(Request request, RequestStatus status) {
        return new RequestSubmission(new RequestData(), request, null, status);
    }

    @Test
    void runsStraightThroughWhenNothingNeedsApproval() {
        Request absence = plainAbsence();
        FlowDefinition flow = definition(List.of(absence, popupFor(0, "adam@glackit.be")));
        stubDefinition(flow);
        saveReturnsArgument();
        when(requestService.processRequest(any(Request.class), eq(ABSENCE_DATA.get(RequestTypeEnum.ABSENCE_REQUEST)), isNull()))
                .thenReturn(submission(absence, RequestStatus.APPROVED));

        TriggerResponse response = runner.instantiateFlow("def", trigger("lais@glackit.be"));

        assertEquals(FlowStatus.SUCCESS, response.status());
        verify(notificationService).send(any(Notification.class), any(Request.class), eq("Absence for Lais"));
        verify(callbackService).send(eq(CALLBACK), isNull(), eq(CallbackService.COMPLETED));
    }

    @Test
    void stopsAtAnApprovalWhileItsRequestIsPending() {
        Request absence = approvableAbsence(1);
        stubDefinition(definition(List.of(absence, approvalOf(0), popupFor(0))));
        saveReturnsArgument();
        when(requestService.processRequest(any(Request.class), any(), isNull()))
                .thenReturn(submission(absence, RequestStatus.PENDING));
        when(requestService.hasPendingSubmission(isNull(), eq(Set.of(0)))).thenReturn(true);

        TriggerResponse response = runner.instantiateFlow("def", trigger("lais@glackit.be"));

        assertEquals(FlowStatus.PENDING, response.status());
        verify(notificationService, never()).send(any(), any(), anyString());
        verify(callbackService).send(eq(CALLBACK), isNull(), eq(CallbackService.PENDING));
    }

    @Test
    void passesAnApprovalOnceNothingIsPending() {
        Request absence = approvableAbsence(1);
        stubDefinition(definition(List.of(absence, approvalOf(0))));
        saveReturnsArgument();
        when(requestService.processRequest(any(Request.class), any(), isNull()))
                .thenReturn(submission(absence, RequestStatus.PENDING));
        when(requestService.hasPendingSubmission(isNull(), eq(Set.of(0)))).thenReturn(false);

        assertEquals(FlowStatus.SUCCESS, runner.instantiateFlow("def", trigger("lais@glackit.be")).status());
    }

    @Test
    void resumeContinuesFromTheApprovalItStoppedAt() {
        FlowDefinition flow = definition(List.of(approvableAbsence(1), approvalOf(0), popupFor(0)));
        FlowInstance instance = new FlowInstance(flow, "Absence for Lais", "lais@glackit.be", ABSENCE_DATA, null);
        instance.nextProcess();
        instance.setFlowStatus(FlowStatus.PENDING);
        when(flowInstanceRepository.existsById("inst")).thenReturn(true);
        when(flowInstanceRepository.claimPending("inst")).thenReturn(true);
        when(flowInstanceRepository.findById("inst")).thenReturn(Optional.of(instance));
        saveReturnsArgument();
        when(requestService.hasPendingSubmission(isNull(), eq(Set.of(0)))).thenReturn(false);

        runner.resumeFlow("inst");

        assertEquals(FlowStatus.SUCCESS, instance.getFlowStatus());
        verify(requestService, never()).processRequest(any(), any(), any());
        verify(notificationService).send(any(Notification.class), any(Request.class), eq("Absence for Lais"));
    }

    @Test
    void resumeKeepsWaitingWhileAnotherRequestIsStillPending() {
        FlowDefinition flow = definition(List.of(approvableAbsence(1), approvalOf(0)));
        FlowInstance instance = new FlowInstance(flow, "Absence for Lais", "lais@glackit.be", ABSENCE_DATA, null);
        instance.nextProcess();
        instance.setFlowStatus(FlowStatus.PENDING);
        when(flowInstanceRepository.existsById("inst")).thenReturn(true);
        when(flowInstanceRepository.claimPending("inst")).thenReturn(true);
        when(flowInstanceRepository.findById("inst")).thenReturn(Optional.of(instance));
        saveReturnsArgument();
        when(requestService.hasPendingSubmission(isNull(), eq(Set.of(0)))).thenReturn(true);

        runner.resumeFlow("inst");

        assertEquals(FlowStatus.PENDING, instance.getFlowStatus());
        assertEquals(1, instance.getStep());
    }

    @Test
    void resumeStopsWhenTheFlowIsNotWaitingOrAnotherCallerClaimedIt() {
        when(flowInstanceRepository.existsById("inst")).thenReturn(true);
        when(flowInstanceRepository.claimPending("inst")).thenReturn(false);

        runner.resumeFlow("inst");

        verify(flowInstanceRepository, never()).findById(any());
        verify(flowInstanceRepository, never()).save(any());
        verify(requestService, never()).hasPendingSubmission(any(), any());
    }

    @Test
    void resumeOfAnUnknownFlowIsNotFound() {
        when(flowInstanceRepository.existsById("missing")).thenReturn(false);

        assertThrows(NotFoundException.class, () -> runner.resumeFlow("missing"));
        verify(flowInstanceRepository, never()).claimPending(any());
    }

    @Test
    void aDeclinedRequestStopsTheFlowInsteadOfCompletingIt() {
        FlowDefinition flow = definition(List.of(approvableAbsence(1), approvalOf(0), popupFor(0)));
        FlowInstance instance = new FlowInstance(flow, "Absence for Lais", "lais@glackit.be", ABSENCE_DATA, CALLBACK);
        instance.nextProcess();
        instance.setFlowStatus(FlowStatus.PENDING);
        when(flowInstanceRepository.existsById("inst")).thenReturn(true);
        when(flowInstanceRepository.claimPending("inst")).thenReturn(true);
        when(flowInstanceRepository.findById("inst")).thenReturn(Optional.of(instance));
        saveReturnsArgument();
        when(requestService.hasDeclinedSubmission(isNull(), eq(Set.of(0)))).thenReturn(true);

        runner.resumeFlow("inst");

        assertEquals(FlowStatus.DECLINED, instance.getFlowStatus());
        assertEquals(1, instance.getStep());
        verify(notificationService, never()).send(any(), any(), anyString());
        verify(callbackService).send(eq(CALLBACK), isNull(), eq(CallbackService.DECLINED));
        verify(callbackService, never()).send(any(), any(), eq(CallbackService.COMPLETED));
    }

    @Test
    void recoveryResumesFlowsWhoseApprovalWasAlreadyDecided() {
        FlowDefinition flow = definition(List.of(approvableAbsence(1), approvalOf(0)));
        FlowInstance stalled = new FlowInstance(flow, "Absence for Lais", "lais@glackit.be", ABSENCE_DATA, null);
        stalled.nextProcess();
        stalled.setFlowStatus(FlowStatus.PENDING);
        when(flowInstanceRepository.findByFlowStatus(FlowStatus.PENDING)).thenReturn(List.of(stalled));
        when(requestService.hasPendingSubmission(isNull(), eq(Set.of(0)))).thenReturn(false);
        when(flowInstanceRepository.existsById(isNull())).thenReturn(true);
        when(flowInstanceRepository.claimPending(isNull())).thenReturn(true);
        when(flowInstanceRepository.findById(isNull())).thenReturn(Optional.of(stalled));
        saveReturnsArgument();

        assertEquals(1, runner.recoverStalledFlows());
        assertEquals(FlowStatus.SUCCESS, stalled.getFlowStatus());
    }

    @Test
    void recoveryLeavesFlowsThatAreStillWaitingOnSomeone() {
        FlowDefinition flow = definition(List.of(approvableAbsence(1), approvalOf(0)));
        FlowInstance waiting = new FlowInstance(flow, "Absence for Lais", "lais@glackit.be", ABSENCE_DATA, null);
        waiting.nextProcess();
        waiting.setFlowStatus(FlowStatus.PENDING);
        when(flowInstanceRepository.findByFlowStatus(FlowStatus.PENDING)).thenReturn(List.of(waiting));
        when(requestService.hasPendingSubmission(isNull(), eq(Set.of(0)))).thenReturn(true);

        assertEquals(0, runner.recoverStalledFlows());
        verify(flowInstanceRepository, never()).claimPending(any());
    }

    @Test
    void invalidRequestDataMarksTheFlowAsFailed() {
        stubDefinition(definition(List.of(plainAbsence())));
        saveReturnsArgument();
        when(requestService.processRequest(any(Request.class), any(), isNull()))
                .thenThrow(new DomainException("Start date cannot be after end date for absence request"));

        DomainException error = assertThrows(DomainException.class,
                () -> runner.instantiateFlow("def", trigger("lais@glackit.be")));

        assertEquals("Start date cannot be after end date for absence request", error.getMessage());
        verify(callbackService).send(eq(CALLBACK), isNull(), eq(CallbackService.FAILED));
    }

    @Test
    void unknownDefinitionIsNotFound() {
        when(flowDefinitionRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> runner.instantiateFlow("missing", trigger("lais@glackit.be")));
    }

    @Test
    void userOutsideTheTriggerListIsRejected() {
        stubDefinition(definition(List.of(plainAbsence())));

        ServiceException error = assertThrows(ServiceException.class,
                () -> runner.instantiateFlow("def", trigger("adam@glackit.be")));

        assertEquals("Flow is not triggerable by adam@glackit.be", error.getMessage());
        verify(flowInstanceRepository, never()).save(any());
    }
}
