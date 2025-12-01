package telerik.project.helpers;

import telerik.project.models.Notification;
import telerik.project.models.User;
import telerik.project.security.auth.SecurityContextUtil;

public final class NotificationFactory {

    private NotificationFactory() {}

    public static Notification create(
            User recipient,
            Long entityId,
            String entityType,
            String actionType
    ) {
        Notification notification = new Notification();
        notification.setActor(SecurityContextUtil.getCurrentUser());
        notification.setRecipient(recipient);
        notification.setEntityId(entityId);
        notification.setEntityType(entityType);
        notification.setActionType(actionType);
        return notification;
    }
}
