package be.ucll.model.strategies.notification;

import java.util.List;

import be.ucll.model.FlowInstance;

public interface NotificationType {
    String getTypeName();

    void send(List<String> users, String message, FlowInstance flowInstance);
}
