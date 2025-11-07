package be.ucll.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Notification")
public class Notification extends Process {

  public NotificationType type;

  public Notification(String title, NotificationType type){
      super(title);
      this.type = type;
  }

  @Override
  public void execute() {
      System.out.println("You've been NOTIFIED");
  }
}
