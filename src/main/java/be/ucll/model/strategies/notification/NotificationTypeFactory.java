package be.ucll.model.strategies.notification;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class NotificationTypeFactory {
    private final Map<String, NotificationType> types;
    
    public NotificationTypeFactory(
        PopUpNotificationType popUpNotificationType,
        EmailNotificationType emailNotificationType) {
        this.types = Map.of(
            "POPUP_NOTIFICATION", popUpNotificationType,
            "EMAIL_NOTIFICATION", emailNotificationType
        );
    }
    
    public NotificationType fromTypeName(String typeName) {
        NotificationType type = types.get(typeName);
        if (type == null) {
            throw new IllegalArgumentException("Unknown request type: " + typeName);
        }
        return type;
    }
}
