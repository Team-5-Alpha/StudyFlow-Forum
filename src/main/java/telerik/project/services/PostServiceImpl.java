package telerik.project.services;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import telerik.project.exceptions.AuthorizationException;
import telerik.project.exceptions.EntityNotFoundException;
import telerik.project.models.Post;
import telerik.project.models.Tag;
import telerik.project.models.User;
import telerik.project.models.filters.PostFilterOptions;
import telerik.project.repositories.PostRepository;
import telerik.project.repositories.specifications.PostSpecifications;
import telerik.project.services.contracts.CommentService;
import telerik.project.services.contracts.NotificationService;
import telerik.project.services.contracts.PostService;
import telerik.project.services.contracts.TagService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final TagService tagService;
    private final NotificationService notificationService;
    private final CommentService commentService;

    public PostServiceImpl(PostRepository postRepository,
                           TagService tagService,
                           NotificationService notificationService,
                           CommentService commentService) {
        this.postRepository = postRepository;
        this.tagService = tagService;
        this.notificationService = notificationService;
        this.commentService = commentService;
    }

    @Override
    public List<Post> getAll(PostFilterOptions filterOptions) {
        return postRepository.findAll(
                PostSpecifications.withFilters(filterOptions),
                PostSpecifications.buildSort(filterOptions)
        );
    }

    @Override
    public Post getById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post", id));
    }

    @Override
    public void create(Post post, User author) {
        post.setAuthor(author);

        //Todo: Tags should be normalized before saving and model should initialize empty collections by default
        Set<Tag> fixedTags = post.getTags().stream()
                .map(t -> tagService.createIfNotExists(t.getName()))
                .collect(Collectors.toSet());

        post.setTags(fixedTags);
        postRepository.save(post);

        //Todo: notify author followers
    }

    @Override
    public void update(Long id, Post updatedPost, User actingUser) {
        Post existing = getById(id);

        if (!actingUser.isAdmin() && !existing.getAuthor().getId().equals(actingUser.getId())) {
            throw new AuthorizationException("You cannot modify this post.");
        }

        existing.setTitle(updatedPost.getTitle());
        existing.setContent(updatedPost.getContent());

        //Todo: Tags should be normalized before saving and model should initialize empty collections by default
        Set<Tag> fixedTags = updatedPost.getTags().stream()
                .map(tag -> tagService.createIfNotExists(tag.getName()))
                .collect(Collectors.toSet());

        existing.setTags(fixedTags);
        postRepository.save(existing);
    }

    @Override
    public void delete(Long id, User actingUser) {
        Post post = getById(id);

        if (!actingUser.isAdmin() && !post.getAuthor().getId().equals(actingUser.getId())) {
            throw new AuthorizationException("You cannot delete this post.");
        }

        post.setIsDeleted(true);
        postRepository.save(post);
    }

    @Override
    public void likePost(Long postId, User user) {
        Post post = getById(postId);

        if (post.getLikedByUsers().contains(user)) {
            return;
        }

        //Todo: LikedByUser should be initialized in the model
        post.getLikedByUsers().add(user);
        postRepository.save(post);

        //Todo: Notify post author
    }

    @Override
    public void unlikePost(Long postId, User user) {
        Post post = getById(postId);

        if (!post.getLikedByUsers().contains(user)) {
            return;
        }

        post.getLikedByUsers().remove(user);
        postRepository.save(post);
    }

    @Override
    public long countByAuthor(Long authorId) {
        return postRepository.countByAuthor_Id(authorId);
    }

    @Override
    public List<Post> getMostRecent() {
        return postRepository.findAll(
                Sort.by(Sort.Direction.DESC, "createdAt")
        ).stream().limit(10).toList();
    }

    @Override
    public List<Post> getMostCommented() {
        //Todo: implement when commentService is ready
        return List.of();
    }

    @Override
    public List<Post> getByTags(List<String> tags) {
        return tags.stream()
                .flatMap(tag -> postRepository.findByTags_Name(tag).stream())
                .distinct()
                .toList();
    }

    public List<Post> getLikedPosts(Long userId) {
        return postRepository.findByLikedByUsers_Id(userId);
    }
}
