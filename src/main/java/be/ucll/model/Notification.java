package be.ucll.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import be.ucll.model.enums.ProcessType;
import be.ucll.model.strategies.notification.NotificationType;
import be.ucll.model.strategies.notification.NotificationTypeFactory;

public class Notification extends Process {

    @JsonIgnore
    private NotificationType notificationType;
    private String notificationTypeName;

    public Notification(String name, String notificationTypeName){
        super(name, ProcessType.NOTIFICATION);
        this.notificationTypeName = notificationTypeName;
        this.notificationType = NotificationTypeFactory.fromTypeName(notificationTypeName);
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public String getNotificationTypeName() {
        return notificationTypeName;
    }
}
