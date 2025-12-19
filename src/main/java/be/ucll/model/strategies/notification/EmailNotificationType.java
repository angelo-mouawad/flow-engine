package be.ucll.model.strategies.notification;

import java.util.List;

import be.ucll.model.FlowInstance;

public class EmailNotificationType implements NotificationType{
    
    @Override
    public String getTypeName() {
        return "EMAIL_NOTIFICATION";
    }

    @Override
    public void send(List<String> users, String message, FlowInstance flowInstance) {
        // TODO
    }
}
