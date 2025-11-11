package telerik.project.repositories.contracts;

import telerik.project.models.Comment;

import java.util.List;

public interface CommentRepository {
    Comment getById(Long id);

    List<Comment> getAll();

//  List<Comment> getByFilter(CommentFilter filter);

    long countByPostId(Long postId);

    void create(Comment comment);

    void update(Comment comment);

    void delete(Long id);
}
