package telerik.project.models.filters;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
public class NotificationFilterOptions {
    private final Optional<Long> actorId;
    private final Optional<Boolean> isRead;
    private final Optional<String> entityType;
    private final Optional<String> actionType;
    private final Optional<LocalDateTime> createdAfter;
    private final Optional<LocalDateTime> createdBefore;
    private final Optional<String> sortBy;
    private final Optional<String> sortOrder;

    private final Integer page;
    private final Integer size;

    public NotificationFilterOptions() {
        this.actorId = Optional.empty();
        this.isRead = Optional.empty();
        this.entityType = Optional.empty();
        this.actionType = Optional.empty();
        this.createdAfter = Optional.empty();
        this.createdBefore = Optional.empty();
        this.sortBy = Optional.empty();
        this.sortOrder = Optional.empty();
        this.page = null;
        this.size = null;
    }

    public NotificationFilterOptions(Long actorId,
                                     Boolean isRead,
                                     String entityType,
                                     String actionType,
                                     LocalDateTime createdAfter,
                                     LocalDateTime createdBefore,
                                     String sortBy,
                                     String sortOrder,
                                     Integer page,
                                     Integer size) {
        this.actorId = Optional.ofNullable(actorId);
        this.isRead = Optional.ofNullable(isRead);
        this.entityType = Optional.ofNullable(entityType);
        this.actionType = Optional.ofNullable(actionType);
        this.createdAfter = Optional.ofNullable(createdAfter);
        this.createdBefore = Optional.ofNullable(createdBefore);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
        this.page = page;
        this.size = size;
    }
}
