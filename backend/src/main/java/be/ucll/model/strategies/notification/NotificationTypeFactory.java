package be.ucll.model.strategies.notification;

import java.util.EnumMap;
import java.util.Map;

import be.ucll.exception.DomainException;
import be.ucll.model.enums.NotificationTypeEnum;

public final class NotificationTypeFactory {

    private static final Map<NotificationTypeEnum, NotificationType> NOTIFICATION_TYPES =
            new EnumMap<>(NotificationTypeEnum.class);

    static {
        register(new EmailNotificationType());
        register(new PopUpNotificationType());
    }

    private NotificationTypeFactory() {
    }

    private static void register(NotificationType type) {
        NOTIFICATION_TYPES.put(type.getTypeName(), type);
    }

    public static NotificationType fromTypeName(NotificationTypeEnum typeName) {
        if (typeName == null) {
            throw new DomainException("Notification type is required");
        }
        NotificationType type = NOTIFICATION_TYPES.get(typeName);
        if (type == null) {
            throw new DomainException("Unknown notification type: " + typeName);
        }
        return type;
    }
}
