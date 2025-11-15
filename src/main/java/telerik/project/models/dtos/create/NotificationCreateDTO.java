package telerik.project.models.dtos.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationCreateDTO {

    @NotNull(message = "recipientId is required.")
    Long recipientId;

    @NotNull(message = "actorId is required.")
    Long actorId;

    @NotNull(message = "entityId is required.")
    Long entityId;

    @NotBlank(message = "entityType cannot be empty.")
    String entityType;

    @NotBlank(message = "actionType cannot be empty.")
    String actionType;
}
