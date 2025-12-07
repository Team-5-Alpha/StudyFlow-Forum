package telerik.project.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import telerik.project.helpers.mappers.NotificationMapper;
import telerik.project.models.dtos.response.ResponseDTO;
import telerik.project.models.dtos.response.NotificationResponseDTO;
import telerik.project.models.filters.NotificationFilterOptions;
import telerik.project.services.contracts.NotificationService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NotificationRestController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    @GetMapping("/private/notifications")
    public ResponseDTO<List<NotificationResponseDTO>> getAll(
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

        List<NotificationResponseDTO> data = notificationService.getAll(filterOptions).stream()
                .map(notificationMapper::toResponse)
                .toList();

        return ResponseDTO.success(data);
    }

    @GetMapping("/private/notifications/{targetNotificationId}")
    public ResponseDTO<NotificationResponseDTO> getById(@PathVariable Long targetNotificationId) {
        NotificationResponseDTO dto =
                notificationMapper.toResponse(notificationService.getById(targetNotificationId));
        return ResponseDTO.success(dto);
    }

    @PutMapping("/private/notifications/{targetNotificationId}/read")
    public ResponseDTO<?> markAsRead(@PathVariable Long targetNotificationId) {
        notificationService.markAsRead(targetNotificationId);
        return ResponseDTO.success("Notification marked as read.");
    }

    @PutMapping("/private/notifications/read-all")
    public ResponseDTO<?> markAllAsRead() {
        notificationService.markAllAsRead();
        return ResponseDTO.success("All notifications marked as read.");
    }

    @DeleteMapping("/private/notifications/{targetNotificationId}")
    public ResponseDTO<?> delete(@PathVariable Long targetNotificationId) {
        notificationService.delete(targetNotificationId);
        return ResponseDTO.success("Notification deleted.");
    }
}