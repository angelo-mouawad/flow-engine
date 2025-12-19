package be.ucll.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import be.ucll.model.enums.NotificationTypeEnum;
import be.ucll.model.enums.ProcessType;
import be.ucll.model.strategies.notification.NotificationType;
import be.ucll.model.strategies.notification.NotificationTypeFactory;

public class Notification extends Process {

    @JsonIgnore
    private NotificationType notificationType;
    private NotificationTypeEnum notificationTypeName;

    private List<String> toNotify = new ArrayList<>();
    private int requestStep;
    private Request request;

    public Notification(String name, NotificationTypeEnum notificationTypeName, List<String> toNotify, int requestStep) {
        super(name, ProcessType.NOTIFICATION);
        this.notificationTypeName = notificationTypeName;
        setNotificationType(notificationTypeName);
        setToNotify(toNotify);
        this.requestStep = requestStep;
    }

    public List<String> getToNotify() {
        if (toNotify == null) {
            return List.of(this.request.getApprovableBy());
        }

        return toNotify;
    }

    public int getRequestStep() {
        return this.requestStep;
    }

    @JsonIgnore
    public String getMessage(){
        return notificationType.generateMessage(request);
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public NotificationTypeEnum getNotificationTypeName() {
        return notificationTypeName;
    }

    public void setRequest(Request request) {
        System.out.println(request.getName());
        this.request = request;
    }

    public void setToNotify(List<String> toNotify) {
        if (toNotify == null) return;

        this.toNotify = toNotify;
    }

    public void setNotificationType(NotificationTypeEnum notificationTypeName) {
        this.notificationType = NotificationTypeFactory.fromTypeName(notificationTypeName);
    }
}
