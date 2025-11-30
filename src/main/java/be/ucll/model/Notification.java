package be.ucll.model;

import be.ucll.model.enums.ProcessType;
import be.ucll.model.strategies.notification.NotificationType;

public class Notification extends Process {

  public NotificationType notificationType;

  public Notification(String name, NotificationType notificationType){
      super(name, ProcessType.NOTIFICATION);
      this.notificationType = notificationType;
  }
}
