package be.ucll.model.strategies.notification;

import be.ucll.model.Request;
import be.ucll.model.enums.NotificationTypeEnum;

public class EmailNotificationType implements NotificationType {

    @Override
    public NotificationTypeEnum getTypeName() {
        return NotificationTypeEnum.EMAIL_NOTIFICATION;
    }

    @Override
    public String generateMessage(Request request, String flowTitle) {
        if (request == null) {
            return "Update from flow \"" + flowTitle + "\"";
        }
        String message = "A new \"" + request.getName() + "\" request was submitted in \"" + flowTitle + "\".";
        return request.isApprovable() ? message + " Please review it in Flow Engine." : message;
    }
}
