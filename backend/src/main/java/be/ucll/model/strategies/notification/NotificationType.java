package be.ucll.model.strategies.notification;

import be.ucll.model.Request;
import be.ucll.model.enums.NotificationTypeEnum;

public interface NotificationType {
    NotificationTypeEnum getTypeName();

    String generateMessage(Request rq);
}
