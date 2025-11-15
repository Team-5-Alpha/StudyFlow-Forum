package telerik.project.models.dtos.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentResponseDTO {

    private Long id;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Boolean isDeleted;

    private Long authorId;

    private String authorUsername;

    private Long postId;

    private Long parentCommentId;

    private long likeCount;
}