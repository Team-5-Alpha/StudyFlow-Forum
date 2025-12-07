package telerik.project.services.contracts;

import telerik.project.models.Notification;
import telerik.project.models.User;
import telerik.project.models.filters.NotificationFilterOptions;

import java.util.List;

public interface NotificationService {

    List<Notification> getAll(NotificationFilterOptions filterOptions);

    Notification getById(Long targetNotificationId);

    void delete(Long targetNotificationId);

    void markAsRead(Long targetNotificationId);

    void markAllAsRead();

    void send(User actor, User recipient, Long entityId, String entityType, String actionType);
}
