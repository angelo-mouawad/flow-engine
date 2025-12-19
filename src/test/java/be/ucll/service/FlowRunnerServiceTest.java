package be.ucll.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import be.ucll.controller.dto.FlowData;
import be.ucll.exception.ServiceException;
import be.ucll.model.*;
import be.ucll.model.enums.FlowStatus;
import be.ucll.model.enums.RequestStatus;
import be.ucll.model.enums.RequestTypeEnum;
import be.ucll.repository.FlowDefinitionRepository;
import be.ucll.repository.FlowInstanceRepository;

@ExtendWith(MockitoExtension.class)
class FlowRunnerServiceTest {

    @Mock
    private FlowInstanceRepository flowInstanceRepository;

    @Mock
    private FlowDefinitionRepository flowDefinitionRepository;

    @Mock
    private RequestService requestService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private TriggerService triggerService;

    @InjectMocks
    private FlowRunnerService flowRunnerService;

    private Map<RequestTypeEnum, Map<String, Object>> testData;

    @BeforeEach
    void setUp() {
        testData = new HashMap<>();
        Map<String, Object> absenceData = new HashMap<>();
        absenceData.put("startDate", "2024-01-01");
        absenceData.put("endDate", "2024-01-05");
        testData.put(RequestTypeEnum.ABSENCE_REQUEST, absenceData);
    }

    @Test
    @DisplayName("Should instantiate and run flow with anyTrigger enabled")
    void testInstantiateFlowWithAnyTrigger() {
        FlowDefinition mockFlowDefinition = mock(FlowDefinition.class);
        when(mockFlowDefinition.isAnyTrigger()).thenReturn(true);

        Request mockRequest = mock(Request.class);
        when(mockRequest.getRequestTypeName()).thenReturn(RequestTypeEnum.ABSENCE_REQUEST);

        when(flowDefinitionRepository.findById("flow-def-123")).thenReturn(Optional.of(mockFlowDefinition));

        FlowInstance savedInstance = mock(FlowInstance.class);
        when(savedInstance.getId()).thenReturn("instance-123");
        when(savedInstance.getData()).thenReturn(testData);
        when(savedInstance.getCurrentProcess())
            .thenReturn(mockRequest)  // First call
            .thenReturn(mockRequest)  // Second call (inside loop)
            .thenReturn(null);        // Exit loop

        when(flowInstanceRepository.save(any(FlowInstance.class))).thenReturn(savedInstance);

        RequestSubmission mockSubmission = mock(RequestSubmission.class);
        when(requestService.processRequest(eq(mockRequest), any())).thenReturn(mockSubmission);

        FlowData flowData = new FlowData("Absence Request", "user@example.com", testData);
        flowRunnerService.instantiateFlow("flow-def-123", "http://callback.url", flowData);

        verify(flowDefinitionRepository).findById("flow-def-123");
        verify(flowInstanceRepository, atLeast(2)).save(any(FlowInstance.class));
        verify(requestService).processRequest(eq(mockRequest), any());
    }

    @Test
    @DisplayName("Should throw exception when flow definition not found")
    void testInstantiateFlowWithInvalidId() {
        when(flowDefinitionRepository.findById("invalid-id")).thenReturn(Optional.empty());

        FlowData flowData = new FlowData("Test Flow", "user@example.com", testData);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            flowRunnerService.instantiateFlow("invalid-id", "http://callback.url", flowData);
        });

        assertEquals("Flow id does not exist", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when user not in triggerableBy list")
    void testInstantiateFlowWithUnauthorizedUser() {
        FlowDefinition mockFlowDefinition = mock(FlowDefinition.class);
        when(mockFlowDefinition.isAnyTrigger()).thenReturn(false);
        when(mockFlowDefinition.getTriggerableBy()).thenReturn(Set.of("authorized@example.com"));
        when(flowDefinitionRepository.findById("flow-def-123")).thenReturn(Optional.of(mockFlowDefinition));

        FlowData flowData = new FlowData("Test Flow", "user@example.com", testData);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            flowRunnerService.instantiateFlow("flow-def-123", "http://callback.url", flowData);
        });

        assertTrue(exception.getMessage().contains("Flow is not triggerable by"));
        assertTrue(exception.getMessage().contains("user@example.com"));
    }

    @Test
    @DisplayName("Should allow authorized user to trigger flow")
    void testInstantiateFlowWithAuthorizedUser() {
        FlowDefinition mockFlowDefinition = mock(FlowDefinition.class);
        when(mockFlowDefinition.isAnyTrigger()).thenReturn(false);
        when(mockFlowDefinition.getTriggerableBy()).thenReturn(Set.of("user@example.com"));
        when(flowDefinitionRepository.findById("flow-def-123")).thenReturn(Optional.of(mockFlowDefinition));

        Request mockRequest = mock(Request.class);
        when(mockRequest.getRequestTypeName()).thenReturn(RequestTypeEnum.CLOCKIN_REQUEST);

        FlowInstance savedInstance = mock(FlowInstance.class);
        when(savedInstance.getId()).thenReturn("instance-123");
        
        Map<RequestTypeEnum, Map<String, Object>> clockinData = new HashMap<>();
        Map<String, Object> clockinDetails = new HashMap<>();
        clockinDetails.put("timestamp", "2024-01-01T09:00:00");
        clockinData.put(RequestTypeEnum.CLOCKIN_REQUEST, clockinDetails);
        
        when(savedInstance.getData()).thenReturn(clockinData);
        when(savedInstance.getCurrentProcess())
            .thenReturn(mockRequest)
            .thenReturn(mockRequest)
            .thenReturn(null);

        when(flowInstanceRepository.save(any(FlowInstance.class))).thenReturn(savedInstance);

        RequestSubmission mockSubmission = mock(RequestSubmission.class);
        when(requestService.processRequest(eq(mockRequest), any())).thenReturn(mockSubmission);

        FlowData flowData = new FlowData("Clock In", "user@example.com", clockinData);
        
        assertDoesNotThrow(() -> {
            flowRunnerService.instantiateFlow("flow-def-123", "http://callback.url", flowData);
        });
    }

    @Test
    @DisplayName("Should pause flow when approval process requires waiting")
    void testFlowPausesOnApprovalWaiting() {
        FlowDefinition mockFlowDefinition = mock(FlowDefinition.class);
        when(mockFlowDefinition.isAnyTrigger()).thenReturn(true);

        Request mockRequest = mock(Request.class);
        when(mockRequest.getRequestTypeName()).thenReturn(RequestTypeEnum.ABSENCE_REQUEST);

        Approval mockApproval = mock(Approval.class);
        when(mockApproval.getRequestSteps()).thenReturn(Set.of(0));

        when(flowDefinitionRepository.findById("flow-def-123")).thenReturn(Optional.of(mockFlowDefinition));

        FlowInstance savedInstance = mock(FlowInstance.class);
        when(savedInstance.getId()).thenReturn("instance-123");
        when(savedInstance.getData()).thenReturn(testData);
        when(savedInstance.getCurrentProcess())
            .thenReturn(mockRequest)   // while condition check
            .thenReturn(mockRequest)   // getCurrentProcess() inside loop
            .thenReturn(mockApproval)  // while condition check
            .thenReturn(mockApproval); // getCurrentProcess() inside loop - then returns

        RequestSubmission mockSubmission = mock(RequestSubmission.class);
        when(mockSubmission.getRequestStep()).thenReturn(0);

        List<RequestSubmission> submissions = new ArrayList<>();
        submissions.add(mockSubmission);
        when(savedInstance.getSubmissions()).thenReturn(submissions);

        when(flowInstanceRepository.save(any(FlowInstance.class))).thenReturn(savedInstance);
        when(requestService.processRequest(eq(mockRequest), any())).thenReturn(mockSubmission);

        FlowData flowData = new FlowData("Absence Request", "user@example.com", testData);
        flowRunnerService.instantiateFlow("flow-def-123", "http://callback.url", flowData);

        verify(savedInstance).setFlowStatus(FlowStatus.PENDING);
        verify(mockRequest).setFlowInstanceId("instance-123");
        verify(mockApproval).setFlowInstanceId("instance-123");
    }

    @Test
    @DisplayName("Should continue flow when approval does not require waiting")
    void testFlowContinuesWhenApprovalDoesNotRequireWaiting() {
        FlowDefinition mockFlowDefinition = mock(FlowDefinition.class);
        when(mockFlowDefinition.isAnyTrigger()).thenReturn(true);

        when(flowDefinitionRepository.findById("flow-def-123")).thenReturn(Optional.of(mockFlowDefinition));

        Request mockRequest = mock(Request.class);
        when(mockRequest.getRequestTypeName()).thenReturn(RequestTypeEnum.TASK_CHANGE_REQUEST);

        Approval mockApproval = mock(Approval.class);
        when(mockApproval.getRequestSteps()).thenReturn(Set.of(5)); // Different step, no waiting

        FlowInstance savedInstance = mock(FlowInstance.class);
        when(savedInstance.getId()).thenReturn("instance-123");
        
        Map<RequestTypeEnum, Map<String, Object>> taskData = new HashMap<>();
        Map<String, Object> taskDetails = new HashMap<>();
        taskDetails.put("taskId", "TASK-123");
        taskDetails.put("newStatus", "In Progress");
        taskData.put(RequestTypeEnum.TASK_CHANGE_REQUEST, taskDetails);
        
        when(savedInstance.getData()).thenReturn(taskData);

        RequestSubmission mockSubmission = mock(RequestSubmission.class);
        when(mockSubmission.getRequestStep()).thenReturn(0);

        List<RequestSubmission> submissions = new ArrayList<>();
        submissions.add(mockSubmission);

        when(savedInstance.getCurrentProcess())
            .thenReturn(mockRequest)   // while check
            .thenReturn(mockRequest)   // inside loop
            .thenReturn(mockApproval)  // while check
            .thenReturn(mockApproval)  // inside loop
            .thenReturn(null);         // exit loop
        when(savedInstance.getSubmissions()).thenReturn(submissions);

        when(flowInstanceRepository.save(any(FlowInstance.class))).thenReturn(savedInstance);
        when(requestService.processRequest(eq(mockRequest), any())).thenReturn(mockSubmission);

        FlowData flowData = new FlowData("Task Change", "user@example.com", taskData);
        flowRunnerService.instantiateFlow("flow-def-123", "http://callback.url", flowData);

        verify(savedInstance, times(2)).nextProcess();
    }

    @Test
    @DisplayName("Should set flow status to SUCCESS when completed")
    void testFlowCompletesSuccessfully() {
        FlowDefinition mockFlowDefinition = mock(FlowDefinition.class);
        when(mockFlowDefinition.isAnyTrigger()).thenReturn(true);

        Request mockRequest = mock(Request.class);
        when(mockRequest.getRequestTypeName()).thenReturn(RequestTypeEnum.ABSENCE_REQUEST);

        when(flowDefinitionRepository.findById("flow-def-123")).thenReturn(Optional.of(mockFlowDefinition));

        FlowInstance savedInstance = mock(FlowInstance.class);
        when(savedInstance.getId()).thenReturn("instance-123");
        when(savedInstance.getData()).thenReturn(testData);
        when(savedInstance.getCurrentProcess())
            .thenReturn(mockRequest)
            .thenReturn(mockRequest)
            .thenReturn(null);

        when(flowInstanceRepository.save(any(FlowInstance.class))).thenReturn(savedInstance);

        RequestSubmission mockSubmission = mock(RequestSubmission.class);
        when(requestService.processRequest(eq(mockRequest), any())).thenReturn(mockSubmission);

        FlowData flowData = new FlowData("Absence Request", "user@example.com", testData);
        flowRunnerService.instantiateFlow("flow-def-123", "http://callback.url", flowData);

        verify(savedInstance).setFlowStatus(FlowStatus.SUCCESS);
    }

    @Test
    @DisplayName("Should set flow status to FAILURE when exception occurs")
    void testFlowFailsOnException() {
        FlowDefinition mockFlowDefinition = mock(FlowDefinition.class);
        when(mockFlowDefinition.isAnyTrigger()).thenReturn(true);

        Request mockRequest = mock(Request.class);
        when(mockRequest.getRequestTypeName()).thenReturn(RequestTypeEnum.CLOCKIN_REQUEST);

        when(flowDefinitionRepository.findById("flow-def-123")).thenReturn(Optional.of(mockFlowDefinition));

        FlowInstance savedInstance = mock(FlowInstance.class);
        when(savedInstance.getId()).thenReturn("instance-123");
        
        Map<RequestTypeEnum, Map<String, Object>> clockinData = new HashMap<>();
        Map<String, Object> clockinDetails = new HashMap<>();
        clockinDetails.put("timestamp", "2024-01-01T09:00:00");
        clockinData.put(RequestTypeEnum.CLOCKIN_REQUEST, clockinDetails);
        
        when(savedInstance.getData()).thenReturn(clockinData);
        when(savedInstance.getCurrentProcess())
            .thenReturn(mockRequest)
            .thenReturn(mockRequest);

        when(flowInstanceRepository.save(any(FlowInstance.class))).thenReturn(savedInstance);
        when(requestService.processRequest(eq(mockRequest), any()))
            .thenThrow(new RuntimeException("Database connection failed"));

        FlowData flowData = new FlowData("Clock In", "user@example.com", clockinData);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            flowRunnerService.instantiateFlow("flow-def-123", "http://callback.url", flowData);
        });

        assertTrue(exception.getMessage().contains("Flow Execution Failed"));
        verify(savedInstance).setFlowStatus(FlowStatus.FAILURE);
    }

    @Test
    @DisplayName("Should resume pending flow successfully")
    void testResumeFlowSuccess() {
        FlowInstance mockInstance = mock(FlowInstance.class);
        when(mockInstance.getFlowStatus()).thenReturn(FlowStatus.PENDING);
        when(mockInstance.getData()).thenReturn(testData);
        when(mockInstance.getCurrentProcess()).thenReturn(null);

        RequestSubmission pendingSubmission = mock(RequestSubmission.class);
        when(pendingSubmission.getStatus()).thenReturn(RequestStatus.PENDING);

        RequestSubmission approvedSubmission = mock(RequestSubmission.class);
        when(approvedSubmission.getStatus()).thenReturn(RequestStatus.APPROVED);

        List<RequestSubmission> submissions = new ArrayList<>();
        submissions.add(pendingSubmission);
        submissions.add(approvedSubmission);

        when(mockInstance.getSubmissions()).thenReturn(submissions);
        when(flowInstanceRepository.findById("instance-123")).thenReturn(Optional.of(mockInstance));
        when(flowInstanceRepository.save(any(FlowInstance.class))).thenReturn(mockInstance);

        flowRunnerService.resumeFlow("instance-123");

        verify(mockInstance).nextProcess();
        verify(mockInstance).setFlowStatus(FlowStatus.ACTIVE);
        verify(mockInstance, atLeast(1)).setUpdatedAt(any(LocalDateTime.class));
        verify(flowInstanceRepository, atLeast(1)).save(mockInstance);
    }

    @Test
    @DisplayName("Should throw exception when resuming non-existent flow")
    void testResumeFlowNotFound() {
        when(flowInstanceRepository.findById("invalid-id")).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            flowRunnerService.resumeFlow("invalid-id");
        });

        assertEquals("Flow Instance not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when resuming non-pending flow")
    void testResumeFlowNotPending() {
        FlowInstance mockInstance = mock(FlowInstance.class);
        when(mockInstance.getFlowStatus()).thenReturn(FlowStatus.ACTIVE);
        when(flowInstanceRepository.findById("instance-123")).thenReturn(Optional.of(mockInstance));

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            flowRunnerService.resumeFlow("instance-123");
        });

        assertEquals("Flow is not PENDING", exception.getMessage());
    }

    @Test
    @DisplayName("Should remove non-pending submissions when resuming flow")
    void testResumeFlowRemovesNonPendingSubmissions() {
        FlowInstance mockInstance = mock(FlowInstance.class);
        when(mockInstance.getFlowStatus()).thenReturn(FlowStatus.PENDING);
        when(mockInstance.getData()).thenReturn(testData);
        when(mockInstance.getCurrentProcess()).thenReturn(null);

        RequestSubmission pendingSubmission1 = mock(RequestSubmission.class);
        when(pendingSubmission1.getStatus()).thenReturn(RequestStatus.PENDING);

        RequestSubmission pendingSubmission2 = mock(RequestSubmission.class);
        when(pendingSubmission2.getStatus()).thenReturn(RequestStatus.PENDING);

        RequestSubmission approvedSubmission = mock(RequestSubmission.class);
        when(approvedSubmission.getStatus()).thenReturn(RequestStatus.APPROVED);

        RequestSubmission declinedSubmission = mock(RequestSubmission.class);
        when(declinedSubmission.getStatus()).thenReturn(RequestStatus.DECLINED);

        List<RequestSubmission> submissions = new ArrayList<>();
        submissions.add(pendingSubmission1);
        submissions.add(pendingSubmission2);
        submissions.add(approvedSubmission);
        submissions.add(declinedSubmission);

        when(mockInstance.getSubmissions()).thenReturn(submissions);
        when(flowInstanceRepository.findById("instance-123")).thenReturn(Optional.of(mockInstance));
        when(flowInstanceRepository.save(any(FlowInstance.class))).thenReturn(mockInstance);

        flowRunnerService.resumeFlow("instance-123");

        verify(mockInstance, times(2)).getSubmissions();
        verify(mockInstance).setFlowStatus(FlowStatus.ACTIVE);
    }
}
