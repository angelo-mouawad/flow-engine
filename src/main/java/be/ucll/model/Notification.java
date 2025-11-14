package be.ucll.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Notification")
public class Notification extends Process {

  public NotificationType notificationType;

  public Notification(String name, NotificationType notificationType){
      super(name, ProcessType.NOTIFICATION);
      this.notificationType = notificationType;
  }

  @Override
  public void execute() {
      System.out.println("You've been NOTIFIED");
  }
}
