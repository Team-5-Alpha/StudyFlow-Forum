package telerik.project.helpers.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import telerik.project.models.Comment;
import telerik.project.models.Post;
import telerik.project.models.User;
import telerik.project.models.dtos.create.CommentCreateDTO;
import telerik.project.models.dtos.response.CommentResponseDTO;
import telerik.project.models.dtos.update.CommentUpdateDTO;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final UserMapper userMapper;

    public Comment fromCreateDTO(CommentCreateDTO dto, User author, Post post, Comment parentComment) {
        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        comment.setAuthor(author);
        comment.setPost(post);
        comment.setParentComment(parentComment);
        return comment;
    }

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
