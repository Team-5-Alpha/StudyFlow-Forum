package telerik.project.models.dtos.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagCreateDTO {

    @NotBlank(message = "name cannot be empty.")
    private String name;
}
