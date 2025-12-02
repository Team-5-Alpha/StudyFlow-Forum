package telerik.project.helpers.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import telerik.project.models.Notification;
import telerik.project.models.dtos.response.NotificationResponseDTO;
import telerik.project.models.dtos.update.NotificationUpdateDTO;

@Component
@RequiredArgsConstructor
public class NotificationMapper {

    private final UserMapper userMapper;

    public void updateNotification(Notification notification, NotificationUpdateDTO dto) {
        notification.setIsRead(dto.isRead());
    }

    public NotificationResponseDTO toResponse(Notification notification) {
        NotificationResponseDTO dto = new NotificationResponseDTO();

        dto.setId(notification.getId());

        dto.setEntityId(notification.getEntityId());
        dto.setEntityType(notification.getEntityType());
        dto.setActionType(notification.getActionType());

        dto.setIsRead(notification.getIsRead());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setActor(userMapper.toSummary(notification.getActor()));

        return dto;
    }
}
