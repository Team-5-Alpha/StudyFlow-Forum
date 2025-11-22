package telerik.project.services;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional(readOnly = true)
    public List<Notification> getAll(Long actingUserId, NotificationFilterOptions filterOptions) {
        User actingUser = getUserById(actingUserId);
        AuthorizationHelper.validateNotBlocked(actingUser);

        Pageable pageable = PaginationUtils.createPageable(
                filterOptions.getPage(),
                filterOptions.getSize(),
                NotificationSpecifications.buildSort(filterOptions)
        );

        Specification<Notification> recipientSpec = (root, query, cb) ->
                cb.equal(root.get("recipient").get("id"), actingUserId);

        Specification<Notification> combined =
                recipientSpec.and(NotificationSpecifications.withFilters(filterOptions));

        return notificationRepository
                .findAll(combined, pageable)
                .getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Notification getById(Long actingUserId, Long id) {
        User actingUser = getUserById(actingUserId);
        AuthorizationHelper.validateNotBlocked(actingUser);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification", id));

        AuthorizationHelper.validateOwner(actingUser, notification.getRecipient());

        return notification;
    }

    @Override
    @Transactional
    public void delete(Long actingUserId, Long id) {
        notificationRepository.delete(getById(actingUserId, id));
    }

    @Override
    @Transactional
    public void markAsRead(Long actingUserId, Long notificationId) {
        AuthorizationHelper.validateNotBlocked(getUserById(actingUserId));

        Notification notification = getById(actingUserId, notificationId);

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long actingUserId) {
        User actingUser = getUserById(actingUserId);
        AuthorizationHelper.validateNotBlocked(actingUser);

        List<Notification> notifications =
                notificationRepository.findByRecipient_IdAndIsReadFalse(actingUserId);

        notifications.forEach(n -> n.setIsRead(true));

        notificationRepository.saveAll(notifications);
    }
    
    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
    }

    @Override
    @Transactional
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
