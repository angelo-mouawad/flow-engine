package be.ucll.model.strategies.notification;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import be.ucll.model.enums.NotificationTypeEnum;

@Component
public class NotificationTypeFactory {
    private static final Map<NotificationTypeEnum, NotificationType> NOTIFICATION_TYPES = new HashMap<>();
    
    static {
        NOTIFICATION_TYPES.put(
                NotificationTypeEnum.EMAIL_NOTIFICATON, 
                new EmailNotificationType());
        NOTIFICATION_TYPES.put(
                NotificationTypeEnum.POPUP_NOTIFICATON, 
                new PopUpNotificationType());
    }
    
    public static NotificationType fromTypeName(NotificationTypeEnum typeName) {
        NotificationType type = NOTIFICATION_TYPES.get(typeName);
        if (type == null) {
            throw new IllegalArgumentException("Unknown notification type: " + typeName);
        }
        return type;
    }
}
