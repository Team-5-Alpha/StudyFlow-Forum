package telerik.project.services.contracts;

import telerik.project.models.Post;
import telerik.project.models.dtos.create.PostCreateDTO;
import telerik.project.models.dtos.update.PostUpdateDTO;
import telerik.project.models.filters.PostFilterOptions;

import java.util.List;

public interface PostService {
    List<Post> getAll(PostFilterOptions filterOptions);

    Post getById(Long targetPostId);

    Post create(PostCreateDTO dto);

    void update(Long targetPostId, PostUpdateDTO dto);

    void delete(Long targetPostId);

    void likePost(Long targetPostId);

    void unlikePost(Long targetPostId);

    long countByAuthor(Long targetUserId);

    List<Post> getByAuthorId(Long targetUserId);

    List<Post> getMostCommented();

    List<Post> getLikedPosts(Long targetUserId);

    long count();
}
