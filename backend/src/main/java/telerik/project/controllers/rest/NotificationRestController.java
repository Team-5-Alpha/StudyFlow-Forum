package telerik.project.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import telerik.project.helpers.mappers.NotificationMapper;
import telerik.project.models.dtos.response.NotificationResponseDTO;
import telerik.project.models.filters.NotificationFilterOptions;
import telerik.project.services.contracts.NotificationService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationRestController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public List<NotificationResponseDTO> getAll(
            @RequestParam(required = false) Long actorId,
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String actionType,
            @RequestParam(required = false) LocalDateTime createdAfter,
            @RequestParam(required = false) LocalDateTime createdBefore,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        NotificationFilterOptions filterOptions = new NotificationFilterOptions(
                actorId, isRead, entityType, actionType, createdAfter, createdBefore,
                sortBy, sortOrder, page, size
        );

        return notificationService.getAll(filterOptions).stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{targetNotificationId}")
    public NotificationResponseDTO getById(@PathVariable Long targetNotificationId) {
        return notificationMapper.toResponse(notificationService.getById(targetNotificationId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/{targetNotificationId}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAsRead(@PathVariable Long targetNotificationId) {
        notificationService.markAsRead(targetNotificationId);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/read-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAsRead() {
        notificationService.markAllAsRead();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{targetNotificationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long targetNotificationId) {
        notificationService.delete(targetNotificationId);
    }
}
