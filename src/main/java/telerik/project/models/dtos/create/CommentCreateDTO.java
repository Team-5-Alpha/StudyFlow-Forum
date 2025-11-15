package telerik.project.models.dtos.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCreateDTO {

    @NotNull(message = "postId is required.")
    private Long postId;

    private Long parentCommentId;

    @NotBlank(message = "Content cannot be empty.")
    String content;


}
