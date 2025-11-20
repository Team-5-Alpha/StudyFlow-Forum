package telerik.project.models.dtos.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import telerik.project.models.dtos.ValidationMessages;

@Getter
@Setter
@NoArgsConstructor
public class CommentCreateDTO {

    @NotBlank(message = ValidationMessages.COMMENT_CONTENT_NOT_NULL_ERROR)
    @Size(min = 4, max = 4096, message = ValidationMessages.COMMENT_CONTENT_LENGTH_ERROR)
    private String content;

    private Long parentCommentId;
}
