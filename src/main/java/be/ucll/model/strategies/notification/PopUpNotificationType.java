package be.ucll.model.strategies.notification;

import java.util.List;

import be.ucll.model.FlowInstance;
import be.ucll.service.WebSocketNotificationService;

public class PopUpNotificationType implements NotificationType{
    
    @Override
    public String getTypeName() {
        return "POPUP_NOTIFICATION";
    }

    private final WebSocketNotificationService wsService;

    public PopUpNotificationType(WebSocketNotificationService wsService) {
        this.wsService = wsService;
    }

    @Override
    public void send(List<String> users, String message, FlowInstance flowInstance) {
        for (String user : users) {
            wsService.sendToUser(user, message);
        }
    }
}
