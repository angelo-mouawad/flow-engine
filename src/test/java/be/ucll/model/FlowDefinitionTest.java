package be.ucll.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.DisplayName;

import be.ucll.exception.DomainException;

@ActiveProfiles("dev")
class FlowDefinitionTest {

    private Set<String> triggerableBy;
    private List<Process> processes;
    
    @BeforeEach
    void setUp() {
        triggerableBy = new HashSet<>(Set.of("user1@glackit.be", "user2@glackit.be"));
        
        Process process1 = mock(Process.class);
        Process process2 = mock(Process.class);
        Process process3 = mock(Process.class);
        processes = new ArrayList<>(List.of(process1, process2, process3));
    }

    @Test
    @DisplayName("Should create valid FlowDefinition with all fields")
    void testValidFlowDefinitionCreation() {
        FlowDefinition definition = new FlowDefinition(
            "Test Flow",
            "Test Description",
            triggerableBy,
            processes
        );
        
        assertEquals("Test Flow", definition.getTitle());
        assertEquals("Test Description", definition.getDescription());
        assertEquals(triggerableBy, definition.getTriggerableBy());
        assertEquals(3, definition.getProcesses().size());
        assertFalse(definition.isAnyTrigger());
        assertNotNull(definition.getUpdatedAt());
    }

    @Test
    @DisplayName("Should set anyTrigger to true when triggerableBy is null")
    void testNullTriggerableBy() {
        FlowDefinition definition = new FlowDefinition(
            "Test Flow",
            "Test Description",
            null,
            processes
        );
        
        assertTrue(definition.isAnyTrigger());
        assertNotNull(definition.getTriggerableBy());
    }

    @Test
    @DisplayName("Should set anyTrigger to true when triggerableBy is empty")
    void testEmptyTriggerableBy() {
        FlowDefinition definition = new FlowDefinition(
            "Test Flow",
            "Test Description",
            new HashSet<>(),
            processes
        );
        
        assertTrue(definition.isAnyTrigger());
    }

    @Test
    @DisplayName("Should set step numbers correctly for all processes")
    void testProcessStepNumbering() {
        Process process1 = mock(Process.class);
        Process process2 = mock(Process.class);
        Process process3 = mock(Process.class);
        List<Process> processList = List.of(process1, process2, process3);
        
        FlowDefinition definition = new FlowDefinition(
            "Test Flow",
            "Test Description",
            triggerableBy,
            processList
        );
        
        verify(process1).setStep(0);
        verify(process2).setStep(1);
        verify(process3).setStep(2);
    }

    @Test
    @DisplayName("Should create immutable copy of processes list")
    void testProcessListImmutability() {
        List<Process> mutableList = new ArrayList<>(processes);
        
        FlowDefinition definition = new FlowDefinition(
            "Test Flow",
            "Test Description",
            triggerableBy,
            mutableList
        );
        
        // Modify original list
        mutableList.add(mock(Process.class));
        
        // Definition's list should remain unchanged
        assertEquals(3, definition.getProcesses().size());
        
        // Should not be able to modify the returned list
        assertThrows(UnsupportedOperationException.class, () -> {
            definition.getProcesses().add(mock(Process.class));
        });
    }

    @Test
    @DisplayName("Should update triggerableBy and reset anyTrigger flag")
    void testSetTriggerableByWithValues() {
        FlowDefinition definition = new FlowDefinition(
            "Test Flow",
            "Test Description",
            null,
            processes
        );
        
        assertTrue(definition.isAnyTrigger());
        
        Set<String> newTriggerableBy = Set.of("user3@glackit.be", "user4@glackit.be");
        definition.setTriggerableBy(newTriggerableBy);
        
        assertFalse(definition.isAnyTrigger());
        assertEquals(newTriggerableBy, definition.getTriggerableBy());
    }

    @Test
    @DisplayName("Should set anyTrigger when updating with empty set")
    void testSetTriggerableByToEmpty() {
        FlowDefinition definition = new FlowDefinition(
            "Test Flow",
            "Test Description",
            triggerableBy,
            processes
        );
        
        assertFalse(definition.isAnyTrigger());
        
        definition.setTriggerableBy(new HashSet<>());
        
        assertTrue(definition.isAnyTrigger());
    }

    @Test
    @DisplayName("Should update processes and renumber steps")
    void testSetProcesses() {
        FlowDefinition definition = new FlowDefinition(
            "Test Flow",
            "Test Description",
            triggerableBy,
            processes
        );
        
        Process newProcess1 = mock(Process.class);
        Process newProcess2 = mock(Process.class);
        List<Process> newProcesses = List.of(newProcess1, newProcess2);
        
        definition.setProcesses(newProcesses);
        
        assertEquals(2, definition.getProcesses().size());
        verify(newProcess1).setStep(0);
        verify(newProcess2).setStep(1);
    }

    @Test
    @DisplayName("Should handle single process correctly")
    void testSingleProcess() {
        Process singleProcess = mock(Process.class);
        
        FlowDefinition definition = new FlowDefinition(
            "Test Flow",
            "Test Description",
            triggerableBy,
            List.of(singleProcess)
        );
        
        assertEquals(1, definition.getProcesses().size());
        verify(singleProcess).setStep(0);
    }

    @Test
    @DisplayName("Should update timestamp correctly")
    void testSetUpdatedAt() {
        FlowDefinition definition = new FlowDefinition(
            "Test Flow",
            "Test Description",
            triggerableBy,
            processes
        );
        
        LocalDateTime originalTime = definition.getUpdatedAt();
        LocalDateTime newTime = LocalDateTime.now().plusDays(1);
        
        definition.setUpdatedAt(newTime);
        
        assertNotEquals(originalTime, definition.getUpdatedAt());
        assertEquals(newTime, definition.getUpdatedAt());
    }

    @Test
    @DisplayName("Should throw exception when getting createdAt without persisted id")
    void testGetCreatedAtWithoutId() {
        FlowDefinition definition = new FlowDefinition(
            "Test Flow",
            "Test Description",
            triggerableBy,
            processes
        );
        
        DomainException exception = assertThrows(DomainException.class, () -> {
            definition.getCreatedAt();
        });
        
        assertEquals("Flow Definition not yet Created", exception.getMessage());
    }

    @Test
    @DisplayName("Should update title correctly")
    void testSetTitle() {
        FlowDefinition definition = new FlowDefinition(
            "Test Flow",
            "Test Description",
            triggerableBy,
            processes
        );
        
        definition.setTitle("Updated Title");
        
        assertEquals("Updated Title", definition.getTitle());
    }

    @Test
    @DisplayName("Should update description correctly")
    void testSetDescription() {
        FlowDefinition definition = new FlowDefinition(
            "Test Flow",
            "Test Description",
            triggerableBy,
            processes
        );
        
        definition.setDescription("Updated Description");
        
        assertEquals("Updated Description", definition.getDescription());
    }

    @Test
    @DisplayName("Should handle empty string title and description")
    void testEmptyStrings() {
        FlowDefinition definition = new FlowDefinition(
            "Test Flow",
            "Test Description",
            triggerableBy,
            processes
        );
        
        DomainException titleException = assertThrows(DomainException.class, () -> {
            definition.setTitle("");
        });
        DomainException descriptionException = assertThrows(DomainException.class, () -> {
            definition.setDescription("");
        });
        
        assertEquals("FlowDefinition Title cannot be blank", titleException.getMessage());
        assertEquals("FlowDefinition Description cannot be blank", descriptionException.getMessage());
    }
}
