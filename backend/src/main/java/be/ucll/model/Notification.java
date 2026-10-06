package be.ucll.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import be.ucll.exception.DomainException;
import be.ucll.model.enums.NotificationTypeEnum;
import be.ucll.model.enums.ProcessType;
import be.ucll.model.strategies.notification.NotificationType;
import be.ucll.model.strategies.notification.NotificationTypeFactory;

public class Notification extends Process {

    private NotificationTypeEnum notificationType;
    private List<String> toNotify = List.of();
    private Integer requestStep;

    protected Notification() {
    }

    @JsonCreator
    public Notification(@JsonProperty("name") String name,
                        @JsonProperty("notificationType") NotificationTypeEnum notificationType,
                        @JsonProperty("toNotify") List<String> toNotify,
                        @JsonProperty("requestStep") Integer requestStep) {
        super(name, ProcessType.NOTIFICATION);
        if (notificationType == null) {
            throw new DomainException("Every notification process needs a notification type");
        }
        this.notificationType = notificationType;
        this.toNotify = List.copyOf(Request.normaliseEmails(toNotify));
        this.requestStep = requestStep;
    }

    public List<String> recipientsFor(Request request) {
        if (!toNotify.isEmpty()) {
            return toNotify;
        }
        return request == null ? List.of() : request.getApprovableBy();
    }

    public String messageFor(Request request, String flowTitle) {
        return getStrategy().generateMessage(request, flowTitle);
    }

    @JsonIgnore
    public NotificationType getStrategy() {
        return NotificationTypeFactory.fromTypeName(notificationType);
    }

    public NotificationTypeEnum getNotificationType() {
        return notificationType;
    }

    public List<String> getToNotify() {
        return toNotify;
    }

    public Integer getRequestStep() {
        return requestStep;
    }
}
