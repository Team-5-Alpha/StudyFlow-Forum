package telerik.project.repositories.contracts;

import telerik.project.models.Post;

import java.util.List;

public interface PostRepository {

    List<Post> getAll();

    Post getById(Long id);

    long countByAuthorId(Long authorId);

    void create(Post post);

    void update(Post post);

    void delete(Long id);
}
