package be.ucll.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Notification")
public class Notification extends Process {

  public NotificationType notifyBy;

  public Notification(NotificationType notif){
    super();
    notifyBy = notif;    
  }

  @Override
  public void execute() {
  }
}
