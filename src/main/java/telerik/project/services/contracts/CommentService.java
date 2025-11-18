package telerik.project.services.contracts;

import telerik.project.models.Comment;
import telerik.project.models.User;
import telerik.project.models.filters.CommentFilterOptions;

import java.util.List;

public interface CommentService {

    List<Comment> getAll(CommentFilterOptions filterOptions);

    Comment getById(Long id);

    void create(Comment comment, Long postId, User author);

    void update(Long id, Comment updatedComment, User actingUser);

    void delete(Long id, User actingUser);

    void likeComment(Long commentId, User user);

    void unlikeComment(Long commentId, User user);

    boolean isLiked(Comment comment, User user);

    List<Comment> getReplies(Long parentCommentId);

    long countByPostId(Long postId);
}