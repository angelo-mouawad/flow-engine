package be.ucll.model.strategies.notification;

import be.ucll.model.Request;
import be.ucll.model.enums.NotificationTypeEnum;

public class PopUpNotificationType implements NotificationType{
    
    @Override
    public NotificationTypeEnum getTypeName() {
        return NotificationTypeEnum.POPUP_NOTIFICATION;
    }

    @Override
    public String generateMessage(Request rq) {
        String message = String.format(
                    "Request \"%s\" has been submitted", 
                    rq.getName());

        if(rq.isApprovable()){
            message += " and is awaiting approval";
        }    

        return message;
    }
}
