package be.ucll.service;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import be.ucll.controller.dto.NotificationMessage;
import be.ucll.model.Notification;
import be.ucll.model.strategies.notification.EmailNotificationType;
import be.ucll.model.strategies.notification.PopUpNotificationType;

@Service
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendNotification(Notification notification) {

        switch (notification.getNotificationType()) {

            case EmailNotificationType email -> 
                sendEmail(notification);

            case PopUpNotificationType popup -> 
                sendPopUp(notification);

            default -> {break;}
        }
    }

    private void sendPopUp(Notification notification){
        String message = notification.getMessage();

        for (String user: notification.getToNotify()) {
            System.out.println(message + " -> " + user);
            messagingTemplate.convertAndSend(
                    "/topic/notification/" + user,
                    new NotificationMessage(message));
        }
    }

    private void sendEmail(Notification notification){
        //TODO
    }
}
