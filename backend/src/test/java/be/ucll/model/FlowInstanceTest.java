package be.ucll.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import be.ucll.exception.DomainException;
import be.ucll.model.enums.FlowStatus;
import be.ucll.model.enums.RequestTypeEnum;

class FlowInstanceTest {

    private FlowDefinition mockFlowDefinition;
    private Map<RequestTypeEnum, Map<String, Object>> testData;
    
    @BeforeEach
    void setUp() {
        mockFlowDefinition = mock(FlowDefinition.class);
        when(mockFlowDefinition.getId()).thenReturn("flow-def-123");
        
        Process process1 = mock(Process.class);
        Process process2 = mock(Process.class);
        when(mockFlowDefinition.getProcesses()).thenReturn(List.of(process1, process2));
        
        testData = new HashMap<>();
        Map<String, Object> innerData = new HashMap<>();
        innerData.put("startDate", "2025-11-10");
        innerData.put("endDate", "2025-11-19");
        innerData.put("submittedBy", "lais@glakit.be");
        innerData.put("reason", "sickness");
        testData.put(RequestTypeEnum.ABSENCE_REQUEST, innerData);
    }

    @Test
    @DisplayName("Should create valid FlowInstance with all required fields")
    void testValidFlowInstanceCreation() {
        FlowInstance instance = new FlowInstance(
            mockFlowDefinition, 
            "Test Flow", 
            "user@glakit.be", 
            testData,
            "http://example.com");
        
        assertEquals("Test Flow", instance.getTitle());
        assertEquals("user@glakit.be", instance.getTriggeredBy());
        assertEquals("http://example.com", instance.getCallingURL());
        assertEquals(FlowStatus.ACTIVE, instance.getFlowStatus());
        assertEquals(0, instance.getStep());
        assertNotNull(instance.getUpdatedAt());
        assertEquals(testData, instance.getData());
    }

    @Test
    @DisplayName("Should throw DomainException when FlowDefinition is null")
    void testNullFlowDefinition() {
        DomainException exception = assertThrows(DomainException.class, () -> {
            new FlowInstance(
                    null, 
                    "Test Flow", 
                    "user@glakit.be", 
                    testData,
                    "http://example.com");
        });
        
        assertEquals("Flow Instance requires a definition", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw DomainException when title is blank")
    void testBlankTitle() {
        DomainException exception = assertThrows(DomainException.class, () -> {
            new FlowInstance(
                    mockFlowDefinition, 
                    "   ", 
                    "user@glakit.be", 
                    testData,
                    "http://example.com"); 
        });
        
        assertEquals("FlowInstance Title cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw DomainException when title is empty")
    void testEmptyTitle() {
        DomainException exception = assertThrows(DomainException.class, () -> {
            new FlowInstance(
                    mockFlowDefinition, 
                    "", 
                    "user@glakit.be", 
                    testData,
                    "http://example.com"); 
        });
        
        assertEquals("FlowInstance Title cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle null triggeredBy gracefully")
    void testNullTriggeredBy() {
        FlowInstance instance = new FlowInstance(
            mockFlowDefinition, 
            "Test Flow", 
            null,
            testData,
            "http://example.com"
        );
        
        assertNull(instance.getTriggeredBy());
    }

    @Test
    @DisplayName("Should increment step correctly with nextProcess")
    void testNextProcess() {
        FlowInstance instance = new FlowInstance(
            mockFlowDefinition, 
            "Test Flow", 
            "user@glakit.be", 
            testData,
            "http://example.com");
        
        assertEquals(0, instance.getStep());
        
        instance.nextProcess();
        assertEquals(1, instance.getStep());
        
        instance.nextProcess();
        assertEquals(2, instance.getStep());
    }

    @Test
    @DisplayName("Should return current process correctly")
    void testGetCurrentProcess() {
        Process process1 = mock(Process.class);
        Process process2 = mock(Process.class);
        when(mockFlowDefinition.getProcesses()).thenReturn(List.of(process1, process2));
        
        FlowInstance instance = new FlowInstance(
            mockFlowDefinition, 
            "Test Flow", 
            "user@glakit.be", 
            testData,
            "http://example.com");
        
        assertEquals(process1, instance.getCurrentProcess());
        
        instance.nextProcess();
        assertEquals(process2, instance.getCurrentProcess());
    }

    @Test
    @DisplayName("Should return null when step exceeds process count")
    void testGetCurrentProcessWhenExceedingSteps() {
        FlowInstance instance = new FlowInstance(
            mockFlowDefinition, 
            "Test Flow", 
            "user@glakit.be", 
            testData,
            "http://example.com");
        
        instance.nextProcess();
        instance.nextProcess();
        
        assertNull(instance.getCurrentProcess());
    }

    @Test
    @DisplayName("Should add submission correctly")
    void testAddSubmission() {
        FlowInstance instance = new FlowInstance(
            mockFlowDefinition, 
            "Test Flow", 
            "user@glakit.be", 
            testData,
            "http://example.com");
        
        RequestSubmission submission = mock(RequestSubmission.class);
        
        assertEquals(0, instance.getSubmissions().size());
        
        instance.addSubmission(submission);
        assertEquals(1, instance.getSubmissions().size());
        assertTrue(instance.getSubmissions().contains(submission));
    }

    @Test
    @DisplayName("Should update flow status correctly")
    void testSetFlowStatus() {
        FlowInstance instance = new FlowInstance(
            mockFlowDefinition, 
            "Test Flow", 
            "user@glakit.be", 
            testData,
            "http://example.com");
        
        assertEquals(FlowStatus.ACTIVE, instance.getFlowStatus());
        
        instance.setFlowStatus(FlowStatus.PENDING);
        assertEquals(FlowStatus.PENDING, instance.getFlowStatus());
    }

    @Test
    @DisplayName("Should update timestamp correctly")
    void testSetUpdatedAt() {
        FlowInstance instance = new FlowInstance(
            mockFlowDefinition, 
            "Test Flow", 
            "user@glakit.be", 
            testData,
            "http://example.com");
        
        LocalDateTime originalTime = instance.getUpdatedAt();
        LocalDateTime newTime = LocalDateTime.now().plusHours(1);
        
        instance.setUpdatedAt(newTime);
        
        assertNotEquals(originalTime, instance.getUpdatedAt());
        assertEquals(newTime, instance.getUpdatedAt());
    }

    @Test
    @DisplayName("Should return flowDefinitionId correctly")
    void testGetFlowDefinitionId() {
        FlowInstance instance = new FlowInstance(
            mockFlowDefinition, 
            "Test Flow", 
            "user@glakit.be", 
            testData,
            "http://example.com");
        
        assertEquals("flow-def-123", instance.getFlowDefinitionId());
    }
}
