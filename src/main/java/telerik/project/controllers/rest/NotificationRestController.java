package telerik.project.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import telerik.project.helpers.mappers.NotificationMapper;
import telerik.project.models.Notification;
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

    @GetMapping
    public List<NotificationResponseDTO> getAll(
            @RequestParam Long actingUserId,
            @RequestParam(required = false) Long actorId,
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String actionType,
            @RequestParam(required = false) LocalDateTime createdAfter,
            @RequestParam(required = false) LocalDateTime createdBefore,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        NotificationFilterOptions filterOptions = new NotificationFilterOptions(
                actorId,
                isRead,
                entityType,
                actionType,
                createdAfter,
                createdBefore,
                sortBy, sortOrder,
                page, size
        );

        return notificationService.getAll(actingUserId, filterOptions).stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public NotificationResponseDTO getById(
            @PathVariable Long id,
            @RequestParam Long actingUserId
    ) {
        Notification notification = notificationService.getById(actingUserId ,id);
        return notificationMapper.toResponse(notification);
    }

    @PutMapping("/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAsRead(
            @PathVariable Long id,
            @RequestParam Long actingUserId
    ) {
        notificationService.markAsRead(actingUserId, id);
    }

    @PutMapping("/read-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAsRead(@RequestParam Long actingUserId) {
        notificationService.markAllAsRead(actingUserId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id,
            @RequestParam Long actingUserId
    ) {
        notificationService.delete(actingUserId, id);
    }
}
