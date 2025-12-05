package be.ucll.model.strategies.notification;

import java.util.HashMap;
import java.util.Map;

public class NotificationTypeFactory {
    private static final Map<String, NotificationType> NOTIFICATION_TYPES = new HashMap<>();
    
    static {
        NOTIFICATION_TYPES.put("POPUP_NOTIFICATION", new PopUpNotificationType());
        NOTIFICATION_TYPES.put("EMAIL_NOTIFICATION", new EmailNotificationType());
    }
    
    public static NotificationType fromTypeName(String typeName) {
        NotificationType type = NOTIFICATION_TYPES.get(typeName);
        if (type == null) {
            throw new IllegalArgumentException("Unknown request type: " + typeName);
        }
        return type;
    }
}
