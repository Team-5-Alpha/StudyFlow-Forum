package telerik.project.services;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import telerik.project.exceptions.EntityNotFoundException;
import telerik.project.helpers.AuthorizationHelper;
import telerik.project.helpers.NotificationFactory;
import telerik.project.models.Notification;
import telerik.project.models.User;
import telerik.project.models.filters.NotificationFilterOptions;
import telerik.project.repositories.NotificationRepository;
import telerik.project.repositories.UserRepository;
import telerik.project.repositories.specifications.NotificationSpecifications;
import telerik.project.services.contracts.NotificationService;
import telerik.project.utils.PaginationUtils;

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

        Pageable pageable = PaginationUtils.createPageable(
                filterOptions.getPage(),
                filterOptions.getPage(),
                NotificationSpecifications.buildSort(filterOptions)
        );

        return notificationRepository
                .findAll(NotificationSpecifications.withFilters(filterOptions), pageable)
                .getContent();
    }

    @Override
    public Notification getById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification", id));
    }

    @Override
    public void delete(Long id, Long userId) {
        AuthorizationHelper.validateNotBlocked(getUserById(userId));

        Notification notification = getById(id);

        AuthorizationHelper.validateOwnerOrAdmin(getUserById(userId), notification.getRecipient());

        notificationRepository.delete(notification);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        AuthorizationHelper.validateNotBlocked(getUserById(userId));

        Notification notification = getById(notificationId);

        AuthorizationHelper.validateOwner(getUserById(userId), notification.getRecipient());

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByRecipient_Id(userId);

        for (Notification notification : notifications) {
            AuthorizationHelper.validateOwner(getUserById(userId), notification.getRecipient());
            notification.setIsRead(true);
        }

        notificationRepository.saveAll(notifications);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
    }

    @Override
    public void send(User actor, User recipient, Long entityId, String entityType, String actionType) {
        Notification notification = NotificationFactory.create(
                actor,
                recipient,
                entityId,
                entityType,
                actionType
        );

        notificationRepository.save(notification);
    }
}
