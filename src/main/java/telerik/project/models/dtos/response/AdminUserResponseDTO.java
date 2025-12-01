package telerik.project.models.dtos.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminUserResponseDTO extends UserResponseDTO{
    private boolean isBlocked;
    private String role;
}
