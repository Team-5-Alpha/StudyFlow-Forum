package telerik.project.models.dtos.update;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentUpdateDTO {

    @NotBlank(message = "Content is required.")
    private String content;
}
