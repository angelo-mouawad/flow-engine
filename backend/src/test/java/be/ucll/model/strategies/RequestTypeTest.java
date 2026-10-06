package be.ucll.model.strategies;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import be.ucll.exception.DomainException;
import be.ucll.model.RequestData;
import be.ucll.model.enums.RequestStatus;
import be.ucll.model.enums.RequestTypeEnum;
import be.ucll.model.strategies.request.RequestType;
import be.ucll.model.strategies.request.RequestTypeFactory;

class RequestTypeTest {

    private static RequestType type(RequestTypeEnum name) {
        return RequestTypeFactory.fromTypeName(name);
    }

    private static RequestData data(Object... pairs) {
        Map<String, Object> fields = new HashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            fields.put((String) pairs[i], pairs[i + 1]);
        }
        return new RequestData(fields);
    }

    private static RequestData absence(String start, String end) {
        return data("Start_Date", start, "End_Date", end, "Reason", "sickness", "Submitted_By", "lais@glackit.be");
    }

    private static RequestData clockIn(String start, String end) {
        return data("Date", "2025-11-19", "Start_Time", start, "End_Time", end, "Submitted_By", "lais@glackit.be");
    }

    @Test
    void validAbsenceWaitsForApproval() {
        assertEquals(RequestStatus.PENDING,
                type(RequestTypeEnum.ABSENCE_REQUEST).validate(absence("2025-11-10", "2025-11-19")));
    }

    @Test
    void absenceRejectsStartAfterEnd() {
        DomainException error = assertThrows(DomainException.class,
                () -> type(RequestTypeEnum.ABSENCE_REQUEST).validate(absence("2025-11-20", "2025-11-19")));
        assertEquals("Start date cannot be after end date for absence request", error.getMessage());
    }

    @Test
    void absenceRejectsMissingOrMalformedDates() {
        RequestType absence = type(RequestTypeEnum.ABSENCE_REQUEST);

        DomainException missing = assertThrows(DomainException.class,
                () -> absence.validate(data("End_Date", "2025-11-19", "Reason", "x", "Submitted_By", "a")));
        assertEquals("Start_Date is required for absence request", missing.getMessage());

        DomainException malformed = assertThrows(DomainException.class,
                () -> absence.validate(absence("10/11/2025", "2025-11-19")));
        assertEquals("Start_Date must be a date like 2025-11-10 for absence request", malformed.getMessage());
    }

    @Test
    void absenceRejectsBlankReason() {
        assertThrows(DomainException.class, () -> type(RequestTypeEnum.ABSENCE_REQUEST).validate(
                data("Start_Date", "2025-11-10", "End_Date", "2025-11-19", "Reason", " ", "Submitted_By", "a")));
    }

    @Test
    void clockInInsideOfficeHoursIsAutoApproved() {
        assertEquals(RequestStatus.APPROVED, type(RequestTypeEnum.CLOCKIN_REQUEST)
                .validate(clockIn("2025-11-19T09:00:00+01:00", "2025-11-19T17:00:00+01:00")));
    }

    @Test
    void clockInOutsideOfficeHoursIsEscalated() {
        assertEquals(RequestStatus.PENDING, type(RequestTypeEnum.CLOCKIN_REQUEST)
                .validate(clockIn("2025-11-19T15:30:00+01:00", "2025-11-19T17:30:00+01:00")));
        assertEquals(RequestStatus.PENDING, type(RequestTypeEnum.CLOCKIN_REQUEST)
                .validate(clockIn("2025-11-19T08:59:00+01:00", "2025-11-19T12:00:00+01:00")));
    }

    @Test
    void clockInRejectsEndBeforeStart() {
        assertThrows(DomainException.class, () -> type(RequestTypeEnum.CLOCKIN_REQUEST)
                .validate(clockIn("2025-11-19T12:00:00+01:00", "2025-11-19T10:00:00+01:00")));
    }

    @Test
    void taskChangeNeedsTwoDifferentTasks() {
        RequestType taskChange = type(RequestTypeEnum.TASK_CHANGE_REQUEST);

        assertEquals(RequestStatus.PENDING, taskChange.validate(
                data("Current_Task", "Backend", "New_Task", "Frontend", "Submitted_By", "a")));
        assertThrows(DomainException.class, () -> taskChange.validate(
                data("Current_Task", "Backend", "New_Task", "backend", "Submitted_By", "a")));
    }

    @Test
    void meetingAcceptsNumericDuration() {
        assertEquals(RequestStatus.PENDING, type(RequestTypeEnum.MEETING_REQUEST).validate(data(
                "Subject", "Sprint review", "Location", "Room 1", "Time", "2025-11-19T10:00:00+01:00",
                "Duration", 60, "Submitted_By", "a")));
    }

    @Test
    void meetingRejectsMissingSubject() {
        assertThrows(DomainException.class, () -> type(RequestTypeEnum.MEETING_REQUEST).validate(data(
                "Location", "Room 1", "Time", "2025-11-19T10:00:00+01:00", "Duration", "1h", "Submitted_By", "a")));
    }

    @Test
    void factoryRejectsMissingType() {
        assertThrows(DomainException.class, () -> RequestTypeFactory.fromTypeName(null));
    }
}
