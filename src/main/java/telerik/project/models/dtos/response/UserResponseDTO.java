package telerik.project.models.dtos.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserResponseDTO {

    private Long id;

    private String firstName;

    private String lastName;

    private String username;

    private String email;

    private String role;

    private String phoneNumber;

    private String profilePhotoURL;

    private Boolean isBlocked;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
