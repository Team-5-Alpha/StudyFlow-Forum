package telerik.project.models.dtos.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserResponseDTO {
    private Long id;

    private String username;
    private String firstName;
    private String lastName;

    private String email;
    private String profilePhotoURL;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
