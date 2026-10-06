package be.ucll.model;

import static be.ucll.model.TestProcesses.approvableAbsence;
import static be.ucll.model.TestProcesses.plainAbsence;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import be.ucll.exception.DomainException;
import be.ucll.model.enums.RequestStatus;
import be.ucll.model.enums.RequestTypeEnum;

class RequestSubmissionTest {

    private RequestSubmission pending(Request request) {
        return new RequestSubmission(new RequestData(), request, "flow-1", RequestStatus.PENDING);
    }

    @Test
    void approvesOnceEnoughApproversSayYes() {
        RequestSubmission submission = pending(approvableAbsence(2));

        submission.decide("adam@glackit.be", RequestStatus.APPROVED);
        assertEquals(RequestStatus.PENDING, submission.getStatus());

        submission.decide("samip@glackit.be", RequestStatus.APPROVED);
        assertEquals(RequestStatus.APPROVED, submission.getStatus());
        assertNotNull(submission.getProcessedAt());
    }

    @Test
    void declinesAsSoonAsTheMinimumCanNoLongerBeReached() {
        RequestSubmission submission = pending(approvableAbsence(2));

        submission.decide("adam@glackit.be", RequestStatus.DECLINED);

        assertEquals(RequestStatus.DECLINED, submission.getStatus());
    }

    @Test
    void oneDeclineIsNotEnoughWhenOnlyOneApprovalIsNeeded() {
        RequestSubmission submission = pending(approvableAbsence(1));

        submission.decide("adam@glackit.be", RequestStatus.DECLINED);
        assertEquals(RequestStatus.PENDING, submission.getStatus());

        submission.decide("samip@glackit.be", RequestStatus.APPROVED);
        assertEquals(RequestStatus.APPROVED, submission.getStatus());
    }

    @Test
    void lookupsNormaliseEmails() {
        RequestSubmission submission = pending(approvableAbsence(1));

        assertTrue(submission.isPendingFor(" Adam@Glackit.be "));
        submission.decide("ADAM@glackit.be", RequestStatus.APPROVED);
        assertFalse(submission.isPendingFor("adam@glackit.be"));
    }

    @Test
    void exposesApprovalsWithTheOriginalEmails() {
        RequestSubmission submission = pending(approvableAbsence(2));
        submission.decide("adam@glackit.be", RequestStatus.APPROVED);

        assertEquals(Map.of("adam@glackit.be", RequestStatus.APPROVED, "samip@glackit.be", RequestStatus.PENDING),
                submission.getApprovals());
    }

    @Test
    void escapingSwapsDotsForUnderscores() {
        assertEquals("first_last@glackit_be", RequestSubmission.escape("First.Last@glackit.be"));
    }

    @Test
    void rejectsSomeoneWhoIsNotAnApprover() {
        RequestSubmission submission = pending(approvableAbsence(1));

        DomainException error = assertThrows(DomainException.class,
                () -> submission.decide("lais@glackit.be", RequestStatus.APPROVED));
        assertTrue(error.getMessage().contains("not an approver"));
    }

    @Test
    void rejectsVotingTwice() {
        RequestSubmission submission = pending(approvableAbsence(2));
        submission.decide("adam@glackit.be", RequestStatus.APPROVED);

        assertThrows(DomainException.class, () -> submission.decide("adam@glackit.be", RequestStatus.APPROVED));
    }

    @Test
    void rejectsDecisionsOnResolvedRequests() {
        RequestSubmission submission = pending(approvableAbsence(1));
        submission.decide("adam@glackit.be", RequestStatus.APPROVED);

        assertThrows(DomainException.class, () -> submission.decide("samip@glackit.be", RequestStatus.DECLINED));
    }

    @Test
    void rejectsDecisionsOtherThanApproveOrDecline() {
        RequestSubmission submission = pending(approvableAbsence(1));

        assertThrows(DomainException.class, () -> submission.decide("adam@glackit.be", RequestStatus.CANCELLED));
    }

    @Test
    void requestWithoutApprovalIsApprovedStraightAway() {
        RequestSubmission submission = plainAbsence().submit(new RequestData(Map.of(
                "Start_Date", "2025-11-10",
                "End_Date", "2025-11-12",
                "Reason", "sick",
                "Submitted_By", "lais@glackit.be")), "flow-1");

        assertEquals(RequestStatus.APPROVED, submission.getStatus());
        assertTrue(submission.getApprovals().isEmpty());
    }

    @Test
    void clockInInsideOfficeHoursIsAutoApprovedEvenWhenApprovable() {
        Request clockIn = new Request("Clock-in", RequestTypeEnum.CLOCKIN_REQUEST, true,
                List.of("adam@glackit.be"), 1);

        RequestSubmission submission = clockIn.submit(new RequestData(Map.of(
                "Date", "2025-11-19",
                "Start_Time", "2025-11-19T09:00:00+01:00",
                "End_Time", "2025-11-19T17:00:00+01:00",
                "Submitted_By", "lais@glackit.be")), "flow-1");

        assertEquals(RequestStatus.APPROVED, submission.getStatus());
        assertEquals("flow-1", submission.getFlowInstanceId());
    }

    @Test
    void newSubmissionHasNoProcessedTimeWhilePending() {
        assertNull(pending(approvableAbsence(1)).getProcessedAt());
    }
}
