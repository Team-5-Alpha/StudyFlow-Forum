package telerik.project.models.dtos.update;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminUpdateDTO extends UserUpdateDTO{

    @Pattern(regexp = "^[0-9+\\-]{6,20}$")
    private String phoneNumber;
}
