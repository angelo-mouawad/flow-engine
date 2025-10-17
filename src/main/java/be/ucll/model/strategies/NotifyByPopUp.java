package be.ucll.model.strategies;

import be.ucll.model.NotificationType;

public class NotifyByPopUp implements NotificationType{

  public NotifyByPopUp(){
  }

  @Override
  public void sendNotification(){
    System.out.println("Notification sent by pop up");
  }
}
