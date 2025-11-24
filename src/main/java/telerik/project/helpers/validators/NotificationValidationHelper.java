package telerik.project.helpers.validators;

import telerik.project.exceptions.InvalidOperationException;
import telerik.project.helpers.ExceptionMessages;
import telerik.project.models.Notification;

public final class NotificationValidationHelper {

    private NotificationValidationHelper() {}

    public static void validateNotAlreadyRead(Notification notification) {
        if (notification.isRead()) {
            throw new InvalidOperationException(
                    String.format(ExceptionMessages.NOTIFICATION_ALREADY_READ, notification.getId())
            );
        }
    }
}
