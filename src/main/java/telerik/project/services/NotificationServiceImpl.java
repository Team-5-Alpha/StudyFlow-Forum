package telerik.project.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import telerik.project.exceptions.AuthorizationException;
import telerik.project.exceptions.EntityNotFoundException;
import telerik.project.models.Notification;
import telerik.project.models.User;
import telerik.project.models.filters.NotificationFilterOptions;
import telerik.project.repositories.NotificationRepository;
import telerik.project.repositories.UserRepository;
import telerik.project.repositories.specifications.NotificationSpecifications;
import telerik.project.services.contracts.NotificationService;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Notification> getAllForUser(Long userId, NotificationFilterOptions filterOptions) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User", userId));

        return notificationRepository.findAll(
                NotificationSpecifications.withFilters(filterOptions),
                NotificationSpecifications.buildSort(filterOptions)
        );
    }

    @Override
    public Notification getById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification", id));
    }

    @Override
    public void create(Notification notification) {
        notificationRepository.save(notification);
    }

    @Override
    public void delete(Long id, Long userId) {
        Notification notification = getById(id);

        if (!notification.getRecipient().getId().equals(userId)) {
            throw new AuthorizationException("You cannot delete someone else's notifications.");
        }

        notificationRepository.delete(notification);
    }

    @Override
    @Transactional
    public void markAsRead(Long id, Long userId) {
        Notification notification = getById(id);

        if (!notification.getRecipient().getId().equals(userId)) {
            throw new AuthorizationException("You cannot modify someone else's notifications.");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByRecipient_Id(userId);
        notifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(notifications);
    }

    @Override
    public void send(User actor, User recipient, Long entityId, String entityType, String actionType) {
        Notification notification = new Notification();
        notification.setActor(actor);
        notification.setRecipient(recipient);
        notification.setEntityId(entityId);
        notification.setEntityType(entityType);
        notification.setActionType(actionType);
        notification.setIsRead(false);

        notificationRepository.save(notification);
    }
}
