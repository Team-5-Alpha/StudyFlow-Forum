package telerik.project.models.dtos.update;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import telerik.project.models.dtos.ValidationMessages;

@Getter
@Setter
@NoArgsConstructor
public class AdminUpdateDTO extends UserUpdateDTO{
    @Size(min = 6, max = 128, message = ValidationMessages.PHONE_NUMBER_LENGTH_ERROR)
    private String phoneNumber;
}
