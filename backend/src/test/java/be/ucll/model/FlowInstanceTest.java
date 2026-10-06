package be.ucll.model;

import static be.ucll.model.TestProcesses.approvableAbsence;
import static be.ucll.model.TestProcesses.approvalOf;
import static be.ucll.model.TestProcesses.popupFor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import be.ucll.exception.DomainException;
import be.ucll.model.enums.FlowStatus;
import be.ucll.model.enums.RequestTypeEnum;

class FlowInstanceTest {

    private final FlowDefinition definition = new FlowDefinition("Absence", "Report an absence", Set.of(),
            List.of(approvableAbsence(1), approvalOf(0), popupFor(0)));

    private FlowInstance instance() {
        return new FlowInstance(definition, "Absence for Lais", "Lais@Glackit.be",
                Map.of(RequestTypeEnum.ABSENCE_REQUEST, Map.of("Reason", "sick")), null);
    }

    @Test
    void startsActiveAtTheFirstProcess() {
        FlowInstance flow = instance();

        assertEquals(FlowStatus.ACTIVE, flow.getFlowStatus());
        assertEquals(0, flow.getStep());
        assertInstanceOf(Request.class, flow.getCurrentProcess());
        assertEquals("lais@glackit.be", flow.getTriggeredBy());
        assertNotNull(flow.getCreatedAt());
    }

    @Test
    void walksThroughProcessesAndEndsWithNull() {
        FlowInstance flow = instance();

        flow.nextProcess();
        assertInstanceOf(Approval.class, flow.getCurrentProcess());
        flow.nextProcess();
        assertInstanceOf(Notification.class, flow.getCurrentProcess());
        flow.nextProcess();
        assertNull(flow.getCurrentProcess());
    }

    @Test
    void returnsDataPerRequestType() {
        assertEquals("sick", instance().getDataFor(RequestTypeEnum.ABSENCE_REQUEST).get("Reason"));
        assertNull(instance().getDataFor(RequestTypeEnum.MEETING_REQUEST));
    }

    @Test
    void blankCallbackUrlIsStoredAsNull() {
        FlowInstance flow = new FlowInstance(definition, "t", "a@b.be", null, "  ");
        assertNull(flow.getCallbackUrl());
    }

    @Test
    void rejectsMissingDefinitionTitleOrTrigger() {
        assertThrows(DomainException.class, () -> new FlowInstance(null, "t", "a@b.be", null, null));
        assertThrows(DomainException.class, () -> new FlowInstance(definition, " ", "a@b.be", null, null));
        assertThrows(DomainException.class, () -> new FlowInstance(definition, "t", null, null, null));
    }

    @Test
    void getProcessRejectsUnknownSteps() {
        assertThrows(DomainException.class, () -> instance().getProcess(5));
    }
}
