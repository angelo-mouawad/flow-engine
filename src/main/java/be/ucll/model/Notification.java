package be.ucll.model;

import org.springframework.data.mongodb.core.mapping.Document;

import be.ucll.model.strategies.notification.NotificationType;

public class Notification extends Process {

  public NotificationType notificationType;

  public Notification(String name, NotificationType notificationType){
      super(name, ProcessType.NOTIFICATION);
      this.notificationType = notificationType;
  }
}
