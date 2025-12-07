package telerik.project.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import telerik.project.exceptions.EntityNotFoundException;
import telerik.project.helpers.AuthorizationHelper;
import telerik.project.helpers.NotificationFactory;
import telerik.project.helpers.validators.NotificationValidationHelper;
import telerik.project.models.Notification;
import telerik.project.models.User;
import telerik.project.models.filters.NotificationFilterOptions;
import telerik.project.repositories.NotificationRepository;
import telerik.project.repositories.specifications.NotificationSpecifications;
import telerik.project.security.auth.SecurityContextUtil;
import telerik.project.services.contracts.NotificationService;
import telerik.project.utils.PaginationUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getAll(NotificationFilterOptions filterOptions) {
        Long currentUserId = SecurityContextUtil.getCurrentUserId();
        AuthorizationHelper.validateNotBlocked(
                buildActingUserPlaceholder(currentUserId)
        );

        Pageable pageable = PaginationUtils.createPageable(
                filterOptions.getPage(),
                filterOptions.getSize(),
                NotificationSpecifications.buildSort(filterOptions)
        );

        Specification<Notification> recipientSpec = (root, query, cb) ->
                cb.equal(root.get("recipient").get("id"), currentUserId);

        Specification<Notification> combined =
                recipientSpec.and(NotificationSpecifications.withFilters(filterOptions));

        return notificationRepository.findAll(combined, pageable).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Notification getById(Long targetNotificationId) {
        return notificationRepository.findById(targetNotificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification", targetNotificationId));
    }

    @Override
    @Transactional
    public void delete(Long targetNotificationId) {
        Notification notification = getById(targetNotificationId);
        notificationRepository.delete(notification);
    }

    @Override
    @Transactional
    public void markAsRead(Long targetNotificationId) {
        Notification targetNotification = getById(targetNotificationId);
        NotificationValidationHelper.validateNotAlreadyRead(targetNotification);
        targetNotification.setIsRead(true);
        notificationRepository.save(targetNotification);
    }

    @Override
    @Transactional
    public void markAllAsRead() {
        Long currentUserId = SecurityContextUtil.getCurrentUserId();
        List<Notification> notifications =
                notificationRepository.findByRecipient_IdAndIsReadFalse(currentUserId);

        notifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(notifications);
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

    private User buildActingUserPlaceholder(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }
}