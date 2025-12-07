package telerik.project.helpers.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import telerik.project.models.Comment;
import telerik.project.models.dtos.response.CommentResponseDTO;
import telerik.project.models.dtos.update.CommentUpdateDTO;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final UserMapper userMapper;

    public void updateComment(Comment comment, CommentUpdateDTO dto) {
        comment.setContent(dto.getContent());
    }

    public CommentResponseDTO toResponse(Comment comment) {
        return toResponse(comment, null);
    }

    public CommentResponseDTO toResponse(Comment comment, Long actingUserId) {
        CommentResponseDTO dto = new CommentResponseDTO();

        dto.setId(comment.getId());
        dto.setContent(comment.getContent());

        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());

        dto.setAuthor(userMapper.toSummary(comment.getAuthor(), actingUserId));

        if (comment.getParentComment() != null) {
            dto.setParentCommentId(comment.getParentComment().getId());
        }

        dto.setLikesCount(comment.getLikedByUsers().size());

        boolean liked = false;
        if (actingUserId != null) {
            liked = comment.getLikedByUsers()
                    .stream()
                    .anyMatch(u -> u.getId().equals(actingUserId));
        }

        dto.setLikedByCurrentUser(liked);

        dto.setLikesCount(comment.getLikedByUsers().size());

        return dto;
    }
}
