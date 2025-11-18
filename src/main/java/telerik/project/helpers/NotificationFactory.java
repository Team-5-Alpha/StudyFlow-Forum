package telerik.project.helpers;

import telerik.project.models.Notification;
import telerik.project.models.User;

import java.time.LocalDateTime;

public final class NotificationFactory {

    private NotificationFactory() {}

    public static Notification cretae(
            User actor,
            User recipient,
            Long entityId,
            String entityType,
            String actionType
    ) {
        Notification notification = new Notification();
        notification.setActor(actor);
        notification.setRecipient(recipient);
        notification.setEntityId(entityId);
        notification.setEntityType(entityType);
        notification.setActionType(actionType);
        return notification;
    }
}
