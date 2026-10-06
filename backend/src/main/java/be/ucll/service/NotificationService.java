package be.ucll.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import be.ucll.controller.dto.NotificationMessage;
import be.ucll.model.Notification;
import be.ucll.model.Request;
import be.ucll.model.enums.NotificationTypeEnum;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final SimpMessageSendingOperations messagingTemplate;

    public NotificationService(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void send(Notification notification, Request request, String flowTitle) {
        List<String> recipients = notification.recipientsFor(request);
        if (recipients.isEmpty()) {
            log.warn("Notification {} in flow \"{}\" has no recipients, skipping", notification.getLabel(), flowTitle);
            return;
        }

        String message = notification.messageFor(request, flowTitle);

        if (notification.getNotificationType() == NotificationTypeEnum.EMAIL_NOTIFICATION) {
            recipients.forEach(recipient ->
                    log.info("Email delivery is not configured, would have emailed {}: {}", recipient, message));
            return;
        }

        for (String recipient : recipients) {
            try {
                messagingTemplate.convertAndSend("/topic/notification/" + recipient,
                        new NotificationMessage(message, "info"));
            } catch (RuntimeException e) {
                log.warn("Could not push notification to {}: {}", recipient, e.getMessage());
            }
        }
    }
}
