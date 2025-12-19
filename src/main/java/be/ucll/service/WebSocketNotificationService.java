package be.ucll.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import be.ucll.controller.dto.NotificationMessage;

@Service
public class WebSocketNotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendToUser(String email, String message) {
        messagingTemplate.convertAndSend(
            "/topic/notification/" + email,
            new NotificationMessage(message)
        );
    } 
}
