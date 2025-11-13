package telerik.project.models.filters;

import lombok.Getter;

import java.util.Optional;

@Getter
public class NotificationFilterOptions {
    private final Optional<Long> recipientId;
    private final Optional<Boolean> isRead;
    private final Optional<String> entityType;
    private final Optional<String> actionType;
    private final Optional<String> sortBy;

    public NotificationFilterOptions() {
        this.recipientId = Optional.empty();
        this.isRead = Optional.empty();
        this.entityType = Optional.empty();
        this.actionType = Optional.empty();
        this.sortBy = Optional.empty();
    }

    public NotificationFilterOptions(Long recipientId,
                                     Boolean isRead,
                                     String entityType,
                                     String actionType,
                                     String sortBy) {
        this.recipientId = Optional.ofNullable(recipientId);
        this.isRead = Optional.ofNullable(isRead);
        this.entityType = Optional.ofNullable(entityType);
        this.actionType = Optional.ofNullable(actionType);
        this.sortBy = Optional.ofNullable(sortBy);
    }
}
