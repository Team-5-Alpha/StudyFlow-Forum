package telerik.project.repositories.contracts;

import telerik.project.models.Post;

import java.util.List;

public interface PostRepository {
    Post getById(Long id);

    List<Post> getAll();

//  List<Post> getByFilter(PostFilter filter);

    long countByAuthorId(Long authorId);

    void create(Post post);

    void update(Post post);

    void delete(Long id);
}
