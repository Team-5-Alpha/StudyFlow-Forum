package telerik.project.models.dtos.update;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import telerik.project.models.dtos.ValidationMessages;

@Getter
@Setter
@NoArgsConstructor
public class AdminUpdateDTO {
    @NotBlank(message = ValidationMessages.FIRST_NAME_NOT_NULL_ERROR)
    @Size(min = 4, max = 32, message = ValidationMessages.FIRST_NAME_LENGTH_ERROR)
    private String firstName;

    @NotBlank(message = ValidationMessages.LAST_NAME_NOT_NULL_ERROR)
    @Size(min = 4, max = 32, message = ValidationMessages.LAST_NAME_LENGTH_ERROR)
    private String lastName;

    @NotBlank(message = ValidationMessages.EMAIL_NOT_NULL_ERROR)
    @Email(message = ValidationMessages.EMAIL_INVALID_ERROR)
    @Size(min = 6, max = 128, message = ValidationMessages.EMAIL_LENGTH_ERROR)
    private String email;

    @NotBlank(message = ValidationMessages.PASSWORD_NOT_NULL_ERROR)
    @Size(min = 6, max = 128, message = ValidationMessages.PASSWORD_LENGTH_ERROR)
    private String password;

    @Size(min = 6, max = 128, message = ValidationMessages.PHONE_NUMBER_LENGTH_ERROR)
    private String phoneNumber;

    @URL(message = ValidationMessages.PROFILE_PHOTO_URL_ERROR)
    @Size(max = 255, message = ValidationMessages.PROFILE_PHOTO_LENGTH_ERROR)
    private String profilePhotoURL;
}
