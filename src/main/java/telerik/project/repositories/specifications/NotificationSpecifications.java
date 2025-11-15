package telerik.project.repositories.specifications;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import telerik.project.models.Notification;
import telerik.project.models.filters.NotificationFilterOptions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationSpecifications {

    public static Specification<Notification> withFilters(NotificationFilterOptions filterOptions) {
        return (root, query, cb) -> {
            List<Predicate> predicate = new ArrayList<>();

            filterOptions.getRecipientId().ifPresent(recipientId ->
                    predicate.add(recipientIdEquals(recipientId).toPredicate(root, query, cb)));

            filterOptions.getActorId().ifPresent(actorId ->
                    predicate.add(actorIdEquals(actorId).toPredicate(root, query, cb)));

            filterOptions.getIsRead().ifPresent(isRead ->
                    predicate.add(isReadEquals(isRead).toPredicate(root, query, cb)));

            filterOptions.getEntityType().ifPresent(entityType ->
                    predicate.add(entityTypeEquals(entityType).toPredicate(root, query, cb)));

            filterOptions.getActionType().ifPresent(actionType ->
                    predicate.add(actionTypeEquals(actionType).toPredicate(root, query, cb)));

            filterOptions.getCreatedAfter().ifPresent(date ->
                    predicate.add(createdAfter(date).toPredicate(root, query, cb)));

            filterOptions.getCreatedBefore().ifPresent(date ->
                    predicate.add(createdBefore(date).toPredicate(root, query, cb)));

            return cb.and(predicate.toArray(new Predicate[0]));
        };
    }

    private static Specification<Notification> recipientIdEquals(Long recipientId) {
        return (root, query, cb) ->
                cb.equal(root.get("recipient").get("id"), recipientId);
    }

    private static Specification<Notification> actorIdEquals(Long actorId) {
        return (root, query, cb) ->
                cb.equal(root.get("actor").get("id"), actorId);
    }

    private static Specification<Notification> isReadEquals(Boolean isRead) {
        return (root, query, cb) ->
                cb.equal(root.get("isRead"), isRead);
    }

    private static Specification<Notification> entityTypeEquals(String entityType) {
        return (root, query, cb) ->
                cb.equal(root.get("entityType"), entityType);
    }

    private static Specification<Notification> actionTypeEquals(String actionType) {
        return (root, query, cb) ->
                cb.equal(root.get("actionType"), actionType);
    }

    private static Specification<Notification> createdAfter(LocalDateTime date) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("createdAt"), date);
    }

    private static Specification<Notification> createdBefore(LocalDateTime date) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("createdAt"), date);
    }

    public static Sort buildSort(NotificationFilterOptions filterOptions) {
        if (filterOptions.getSortBy().isEmpty()) {
            return Sort.unsorted();
        }

        String sortBy = filterOptions.getSortBy().get();
        Sort.Direction direction = filterOptions.getSortOrder()
                .filter(order -> order.equalsIgnoreCase("desc"))
                .map(order -> Sort.Direction.DESC).orElse(Sort.Direction.ASC);

        return switch (sortBy) {
            case "createdAt" -> Sort.by(direction, "createdAt");
            case "entityType" -> Sort.by(direction, "entityType");
            default -> Sort.unsorted();
        };
    }
}
