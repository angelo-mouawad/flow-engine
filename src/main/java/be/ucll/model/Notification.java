package be.ucll.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Notification")
public class Notification extends Process {

  public NotificationType notifyBy;

  public Notification(NotificationType notifyBy){
    super();
    this.notifyBy = notifyBy;    
  }

  @Override
  public void execute() {
  }
}
