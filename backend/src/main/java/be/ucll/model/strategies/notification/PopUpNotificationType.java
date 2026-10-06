package be.ucll.model.strategies.notification;

import be.ucll.model.Request;
import be.ucll.model.enums.NotificationTypeEnum;

public class PopUpNotificationType implements NotificationType {

    @Override
    public NotificationTypeEnum getTypeName() {
        return NotificationTypeEnum.POPUP_NOTIFICATION;
    }

    @Override
    public String generateMessage(Request request, String flowTitle) {
        if (request == null) {
            return "Flow \"" + flowTitle + "\" has an update for you";
        }
        String message = "Request \"" + request.getName() + "\" in \"" + flowTitle + "\" has been submitted";
        return request.isApprovable() ? message + " and is awaiting approval" : message;
    }
}
