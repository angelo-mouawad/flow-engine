package be.ucll.model.strategies.notification;

public class EmailNotificationType implements NotificationType{
    
    @Override
    public String getTypeName() {
        return "EMAIL_NOTIFICATION";
    }
}
