package telerik.project.models.dtos.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentResponseDTO {

    private Long id;

    private String content;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private UserSummaryDTO author;

    private int likesCount;

    private Long parentCommentId;
    private boolean likedByCurrentUser;
}