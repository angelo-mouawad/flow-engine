package be.ucll.model;

import java.util.List;
import java.util.Set;

import be.ucll.model.enums.NotificationTypeEnum;
import be.ucll.model.enums.RequestTypeEnum;

public final class TestProcesses {

    public static final List<String> MANAGERS = List.of("adam@glackit.be", "samip@glackit.be");

    private TestProcesses() {
    }

    public static Request approvableAbsence(int minApprovals) {
        return new Request("Absence", RequestTypeEnum.ABSENCE_REQUEST, true, MANAGERS, minApprovals);
    }

    public static Request plainAbsence() {
        return new Request("Absence", RequestTypeEnum.ABSENCE_REQUEST, false, List.of(), 0);
    }

    public static Approval approvalOf(Integer... steps) {
        return new Approval("Approval", Set.of(steps));
    }

    public static Notification popupFor(Integer requestStep, String... recipients) {
        return new Notification("Notify", NotificationTypeEnum.POPUP_NOTIFICATION, List.of(recipients), requestStep);
    }
}
