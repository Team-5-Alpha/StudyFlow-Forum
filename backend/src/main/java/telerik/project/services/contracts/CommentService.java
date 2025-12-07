package telerik.project.services.contracts;

import telerik.project.models.Comment;
import telerik.project.models.filters.CommentFilterOptions;

import java.util.List;

public interface CommentService {

    List<Comment> getAll(CommentFilterOptions filterOptions);

    Comment getById(Long targetCommentId);

    void create(Comment comment, Long targetPostId);

    void update(Long targetCommentId, Comment updatedComment);

    void delete(Long targetCommentId);

    void likeComment(Long targetCommentId);

    void unlikeComment(Long targetCommentId);

    List<Comment> getReplies(Long parentCommentId);

    long countByPostId(Long targetPostId);
}