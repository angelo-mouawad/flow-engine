package be.ucll.model.strategies;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import be.ucll.model.NotificationType;

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "type"
)
@JsonSubTypes({
  @JsonSubTypes.Type(value = NotifyByPopUp.class, name = "popup")
})
public class NotifyByPopUp implements NotificationType{

  public NotifyByPopUp(){
  }

  @Override
  public void sendNotification(){
    System.out.println("Notification sent by pop up");
  }
}
