package be.ucll.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import be.ucll.model.enums.NotificationTypeEnum;
import be.ucll.model.enums.ProcessType;
import be.ucll.model.strategies.notification.NotificationType;
import be.ucll.model.strategies.notification.NotificationTypeFactory;

public class Notification extends Process {

    @JsonIgnore
    private NotificationType notificationType;

    @JsonProperty("notificationType")
    private NotificationTypeEnum notificationTypeName;

    private List<String> toNotify = new ArrayList<>();
    private int requestStep;

    @JsonIgnore
    private Request request;

    public Notification() { super(); }

    public Notification(String name, NotificationTypeEnum notificationTypeName, List<String> toNotify, int requestStep) {
        super(name, ProcessType.NOTIFICATION);
        setNotificationTypeName(notificationTypeName);
        // setNotificationType(notificationTypeName);
        setToNotify(toNotify);
        this.requestStep = requestStep;
    }

    public List<String> getToNotify() {
        if ((toNotify == null || toNotify.isEmpty()) && this.request != null) {
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
        this.request = request;
    }

    public void setToNotify(List<String> toNotify) {
        if (toNotify == null) return;

        this.toNotify = toNotify;
    }

    @JsonProperty("notificationType")
    public void setNotificationTypeName(NotificationTypeEnum notificationTypeName) {
        this.notificationTypeName = notificationTypeName;
        if (notificationTypeName != null) {
            this.notificationType = NotificationTypeFactory.fromTypeName(notificationTypeName);
        }
    }

}
