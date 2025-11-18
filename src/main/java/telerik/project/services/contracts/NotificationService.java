package telerik.project.services.contracts;

import telerik.project.models.Notification;
import telerik.project.models.User;
import telerik.project.models.filters.NotificationFilterOptions;

import java.util.List;

public interface NotificationService {

    List<Notification> getAllForUser(Long userId, NotificationFilterOptions filterOptions);

    Notification getById(Long id);

    void delete(Long id, Long userId);

    void markAsRead(Long id, Long userId);

    void markAllAsRead(Long userId);

    void send(User actor, User recipient, Long entityId, String entityType, String actionType);
}
