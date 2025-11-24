package telerik.project.models.dtos.update;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import telerik.project.models.dtos.ValidationMessages;

@Getter
@Setter
@NoArgsConstructor
public class UserUpdateDTO {

    @Size(min = 4, max = 32, message = ValidationMessages.FIRST_NAME_LENGTH_ERROR)
    private String firstName;

    @Size(min = 4, max = 32, message = ValidationMessages.LAST_NAME_LENGTH_ERROR)
    private String lastName;

    @Email(message = ValidationMessages.EMAIL_INVALID_ERROR)
    @Size(min = 6, max = 128, message = ValidationMessages.EMAIL_LENGTH_ERROR)
    private String email;

    @Size(min = 6, max = 128, message = ValidationMessages.PASSWORD_LENGTH_ERROR)
    private String password;

    @URL(message = ValidationMessages.PROFILE_PHOTO_URL_ERROR)
    @Size(max = 255, message = ValidationMessages.PROFILE_PHOTO_LENGTH_ERROR)
    private String profilePhotoURL;
}
