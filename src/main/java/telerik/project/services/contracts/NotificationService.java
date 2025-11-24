package telerik.project.services.contracts;

import telerik.project.models.Notification;
import telerik.project.models.User;
import telerik.project.models.filters.NotificationFilterOptions;

import java.util.List;

public interface NotificationService {

    List<Notification> getAll(Long actingUserId, NotificationFilterOptions filterOptions);

    Notification getById(Long actingUserId, Long id);

    void delete(Long actingUserId, Long id);

    void markAsRead(Long actingUserId, Long id);

    void markAllAsRead(Long actingUserId);

    void send(User actor, User recipient, Long entityId, String entityType, String actionType);
}
