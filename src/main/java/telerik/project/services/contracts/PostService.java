package telerik.project.services.contracts;

import telerik.project.models.Post;
import telerik.project.models.User;
import telerik.project.models.filters.PostFilterOptions;

import java.util.List;

public interface PostService {

    List<Post> getAll(PostFilterOptions filterOptions);

    Post getById(Long id);

    void create(Post post, User author);

    void update(Long id, Post updatedPost, User actingUser);

    void delete(Long id, User actingUser);

    void likePost(Long postId, User user);

    void unlikePost(Long postId, User user);

    long countByAuthor(Long authorId);

    List<Post> getMostCommented();

    List<Post> getMostRecent();

    List<Post> getByTags(List<String> tags);

    List<Post> getLikedPosts(Long userId);

}
