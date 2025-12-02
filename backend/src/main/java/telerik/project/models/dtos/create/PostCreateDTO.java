package telerik.project.models.dtos.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import telerik.project.models.dtos.ValidationMessages;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class PostCreateDTO {

    @NotBlank(message = ValidationMessages.POST_TITLE_NOT_NULL_ERROR)
    @Size(min = 16, max = 64, message = ValidationMessages.POST_TITLE_LENGTH_ERROR)
    private String title;

    @NotBlank(message = ValidationMessages.POST_CONTENT_NOT_NULL_ERROR)
    @Size(min = 32, max = 8192, message = ValidationMessages.POST_CONTENT_LENGTH_ERROR)
    private String content;
}
