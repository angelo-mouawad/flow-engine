package be.ucll.model;

import static be.ucll.model.TestProcesses.approvableAbsence;
import static be.ucll.model.TestProcesses.approvalOf;
import static be.ucll.model.TestProcesses.plainAbsence;
import static be.ucll.model.TestProcesses.popupFor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import be.ucll.exception.DomainException;

class FlowDefinitionTest {

    private FlowDefinition definition(Set<String> triggers, List<Process> processes) {
        return new FlowDefinition("Absence", "Report an absence", triggers, processes);
    }

    @Test
    void numbersProcessesInOrder() {
        FlowDefinition flow = definition(Set.of(), List.of(approvableAbsence(1), approvalOf(0), popupFor(0)));

        assertEquals(List.of(0, 1, 2), flow.getProcesses().stream().map(Process::getStep).toList());
    }

    @Test
    void emptyTriggerListMeansAnyoneCanTrigger() {
        FlowDefinition flow = definition(Set.of(), List.of(plainAbsence()));

        assertTrue(flow.isAnyTrigger());
        assertTrue(flow.canBeTriggeredBy("someone@example.com"));
    }

    @Test
    void triggerCheckIgnoresCaseAndWhitespace() {
        FlowDefinition flow = definition(Set.of(" Lais@Glackit.be "), List.of(plainAbsence()));

        assertTrue(flow.canBeTriggeredBy("lais@glackit.be"));
        assertFalse(flow.canBeTriggeredBy("adam@glackit.be"));
        assertFalse(flow.canBeTriggeredBy(null));
    }

    @Test
    void rejectsBlankTitleAndDescription() {
        assertThrows(DomainException.class,
                () -> new FlowDefinition(" ", "description", Set.of(), List.of(plainAbsence())));
        assertThrows(DomainException.class,
                () -> new FlowDefinition("title", null, Set.of(), List.of(plainAbsence())));
    }

    @Test
    void rejectsEmptyProcessList() {
        DomainException error = assertThrows(DomainException.class, () -> definition(Set.of(), List.of()));
        assertEquals("A flow definition needs at least one process", error.getMessage());
    }

    @Test
    void rejectsApprovalThatPointsAtANonApprovableRequest() {
        DomainException error = assertThrows(DomainException.class,
                () -> definition(Set.of(), List.of(plainAbsence(), approvalOf(0))));
        assertTrue(error.getMessage().contains("points at step 0"));
    }

    @Test
    void rejectsApprovalThatPointsForward() {
        assertThrows(DomainException.class,
                () -> definition(Set.of(), List.of(approvalOf(1), approvableAbsence(1))));
    }

    @Test
    void rejectsApprovalWithoutSteps() {
        assertThrows(DomainException.class,
                () -> definition(Set.of(), List.of(approvableAbsence(1), approvalOf())));
    }

    @Test
    void rejectsNotificationWithNobodyToNotify() {
        DomainException error = assertThrows(DomainException.class,
                () -> definition(Set.of(), List.of(plainAbsence(), popupFor(0))));
        assertTrue(error.getMessage().contains("nobody to notify"));
    }

    @Test
    void notificationFallsBackToApproversOfItsRequest() {
        FlowDefinition flow = definition(Set.of(), List.of(approvableAbsence(1), approvalOf(0), popupFor(0)));
        Notification notification = (Notification) flow.getProcesses().get(2);
        Request request = (Request) flow.getProcesses().get(0);

        assertEquals(TestProcesses.MANAGERS, notification.recipientsFor(request));
    }

    @Test
    void rejectsNotificationPointingAtSomethingThatIsNotARequest() {
        assertThrows(DomainException.class,
                () -> definition(Set.of(), List.of(approvableAbsence(1), approvalOf(0), popupFor(1, "a@b.be"))));
    }

    @Test
    void rejectsProcessWithoutName() {
        Request unnamed = new Request(" ", be.ucll.model.enums.RequestTypeEnum.ABSENCE_REQUEST, false, null, null);
        assertThrows(DomainException.class, () -> definition(Set.of(), List.of(unnamed)));
    }

    @Test
    void rejectsApprovableRequestWithTooManyRequiredApprovals() {
        DomainException error = assertThrows(DomainException.class,
                () -> definition(Set.of(), List.of(approvableAbsence(3), approvalOf(0))));
        assertTrue(error.getMessage().contains("between 1 and 2"));
    }

    @Test
    void zeroMinApprovalsMeansEveryApprover() {
        assertEquals(2, approvableAbsence(0).getMinApprovals());
    }
}
