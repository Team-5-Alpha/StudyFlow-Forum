package telerik.project.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @GetMapping
    public List<NotificationResponseDTO> getAll(
            @RequestHeader("X-User-Id") Long actingUserId,
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

        return notificationService.getAll(actingUserId, filterOptions).stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public NotificationResponseDTO getById(
            @RequestHeader("X-User-Id") Long actingUserId,
            @PathVariable Long id
    ) {
        return notificationMapper.toResponse(notificationService.getById(actingUserId, id));
    }

    @PutMapping("/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAsRead(
            @RequestHeader("X-User-Id") Long actingUserId,
            @PathVariable Long id
    ) {
        notificationService.markAsRead(actingUserId, id);
    }

    @PutMapping("/read-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAsRead(@RequestHeader("X-User-Id") Long actingUserId) {
        notificationService.markAllAsRead(actingUserId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @RequestHeader("X-User-Id") Long actingUserId,
            @PathVariable Long id
    ) {
        notificationService.delete(actingUserId, id);
    }
}
