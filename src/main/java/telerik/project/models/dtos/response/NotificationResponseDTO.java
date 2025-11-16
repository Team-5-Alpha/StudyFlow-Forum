package telerik.project.models.dtos.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationResponseDTO {

    private Long id;

    private Long recipientId;

    private String recipientUsername;

    private Long actorId;

    private String actorUsername;

    private Long entityId;

    private String entityType;

    private String actionType;

    private Boolean isRead;

    private LocalDateTime createdAt;
}
