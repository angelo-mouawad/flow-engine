package be.ucll.model.strategies.notification;

import be.ucll.model.Request;
import be.ucll.model.enums.NotificationTypeEnum;

public class EmailNotificationType implements NotificationType{
    
    @Override
    public NotificationTypeEnum getTypeName() {
        return NotificationTypeEnum.EMAIL_NOTIFICATION;
    }

    @Override
    public String generateMessage(Request rq) {
        return null;
    }
}
