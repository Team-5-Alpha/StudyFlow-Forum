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
        AuthorizationHelper.validateNotBlocked();

        Pageable pageable = PaginationUtils.createPageable(
                filterOptions.getPage(),
                filterOptions.getSize(),
                NotificationSpecifications.buildSort(filterOptions)
        );

        Specification<Notification> recipientSpec = (root, query, cb) ->
                cb.equal(root.get("recipient").get("id"), SecurityContextUtil.getCurrentUser().getId());

        Specification<Notification> combined =
                recipientSpec.and(NotificationSpecifications.withFilters(filterOptions));

        return notificationRepository.findAll(combined, pageable).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Notification getById(Long targetNotificationId) {
        AuthorizationHelper.validateNotBlocked();

        Notification notification = notificationRepository.findById(targetNotificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification", targetNotificationId));

        AuthorizationHelper.validateOwner(notification.getRecipient());

        return notification;
    }

    @Override
    @Transactional
    public void delete(Long targetNotificationId) {
        AuthorizationHelper.validateNotBlocked();

        Notification notification = getById(targetNotificationId);

        notificationRepository.delete(notification);
    }

    @Override
    @Transactional
    public void markAsRead(Long targetNotificationId) {
        AuthorizationHelper.validateNotBlocked();

        Notification targetNotification = getById(targetNotificationId);

        NotificationValidationHelper.validateNotAlreadyRead(targetNotification);

        targetNotification.setIsRead(true);
        notificationRepository.save(targetNotification);
    }

    @Override
    @Transactional
    public void markAllAsRead() {
        AuthorizationHelper.validateNotBlocked();

        List<Notification> notifications =
                notificationRepository.findByRecipient_IdAndIsReadFalse(SecurityContextUtil.getCurrentUser().getId());

        notifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(notifications);
    }

    @Override
    @Transactional
    public void send(User recipient, Long entityId, String entityType, String actionType) {
        Notification notification = NotificationFactory.create(
                recipient,
                entityId,
                entityType,
                actionType
        );

        notificationRepository.save(notification);
    }
}
