package telerik.project.repositories.contracts;

import telerik.project.models.Comment;

import java.util.List;

public interface CommentRepository {

    List<Comment> getAll();

    Comment getById(Long id);

    long countByPostId(Long postId);

    void create(Comment comment);

    void update(Comment comment);

    void delete(Long id);
}
