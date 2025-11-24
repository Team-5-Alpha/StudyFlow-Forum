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
        CommentResponseDTO dto = new CommentResponseDTO();

        dto.setId(comment.getId());
        dto.setContent(comment.getContent());

        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());

        dto.setAuthor(userMapper.toSummary(comment.getAuthor()));

        dto.setParentCommentId(comment.getParentComment() != null
                ? comment.getParentComment().getId()
                : null);
        dto.setLikeCount(comment.getLikedByUsers().size());
        return dto;
    }
}
