package telerik.project.models.dtos.update;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDTO {

    @NotBlank(message = "firstName cannot be empty.")
    String firstName;

    @NotBlank(message = "lastName cannot be empty.")
    String lastName;

    String phoneNumber;

    String profilePhotoURL;
}
