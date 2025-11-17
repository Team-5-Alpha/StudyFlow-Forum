package telerik.project.services;

import jakarta.transaction.Transactional;
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

    public PostServiceImpl(PostRepository postRepository,
                           TagService tagService,
                           NotificationService notificationService) {
        this.postRepository = postRepository;
        this.tagService = tagService;
        this.notificationService = notificationService;
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
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post", id));

        if (Boolean.TRUE.equals(post.getIsDeleted())) {
            throw new EntityNotFoundException("Post", id);
        }

        return post;
    }

    @Override
    public void create(Post post, User author) {
        if (Boolean.TRUE.equals(author.getIsBlocked())) {
            throw new AuthorizationException("Blocked users cannot create posts.");
        }

        post.setAuthor(author);

        Set<Tag> fixedTags = post.getTags().stream()
                .map(t -> tagService.createIfNotExists(t.getName()))
                .collect(Collectors.toSet());

        post.setTags(fixedTags);
        postRepository.save(post);

        for (User follower : author.getFollowers()) {
            notificationService.send(author, follower, post.getId(), "POST", "CREATE");
        }
    }

    @Override
    @Transactional
    public void update(Long postId, Post updatedPost, User actingUser) {
        if (Boolean.TRUE.equals(actingUser.getIsBlocked())) {
            throw new AuthorizationException("Blocked users cannot update posts.");
        }

        Post existing = getById(postId);

        if (!actingUser.isAdmin() && !existing.getAuthor().getId().equals(actingUser.getId())) {
            throw new AuthorizationException("You cannot modify this post.");
        }

        existing.setTitle(updatedPost.getTitle());
        existing.setContent(updatedPost.getContent());

        Set<Tag> fixedTags = updatedPost.getTags().stream()
                .map(tag -> tagService.createIfNotExists(tag.getName()))
                .collect(Collectors.toSet());

        existing.setTags(fixedTags);
        postRepository.save(existing);
    }

    @Override
    public void delete(Long postId, User actingUser) {
        if (Boolean.TRUE.equals(actingUser.getIsBlocked())) {
            throw new AuthorizationException("Blocked users cannot delete posts.");
        }

        Post post = getById(postId);

        if (!actingUser.isAdmin() && !post.getAuthor().getId().equals(actingUser.getId())) {
            throw new AuthorizationException("You cannot delete this post.");
        }

        post.setIsDeleted(true);
        postRepository.save(post);
    }

    @Override
    @Transactional
    public void likePost(Long postId, User user) {
        if (Boolean.TRUE.equals(user.getIsBlocked())) {
            throw new AuthorizationException("Blocked users cannot like posts.");
        }

        Post post = getById(postId);

        if (!post.getLikedByUsers().contains(user)) {
            post.getLikedByUsers().add(user);
            postRepository.save(post);

            notificationService.send(user, post.getAuthor(), postId, "POST", "LIKE");
        }
    }

    @Override
    @Transactional
    public void unlikePost(Long postId, User user) {
        if (Boolean.TRUE.equals(user.getIsBlocked())) {
            throw new AuthorizationException("Blocked users cannot unlike posts.");
        }

        Post post = getById(postId);

        if (post.getLikedByUsers().contains(user)) {
            post.getLikedByUsers().remove(user);
            postRepository.save(post);
        }
    }

    @Override
    public long countByAuthor(Long authorId) {
        return postRepository.countByAuthor_Id(authorId);
    }

    @Override
    public List<Post> getByAuthorId(Long authorId) {
        return postRepository.findByAuthor_Id(authorId);
    }

    @Override
    public List<Post> getMostRecent() {
        return postRepository.findAll(
                Sort.by(Sort.Direction.DESC, "createdAt")
                ).stream()
                .filter(p -> !Boolean.TRUE.equals(p.getIsDeleted()))
                .limit(10)
                .toList();
    }

    @Override
    public List<Post> getMostCommented() {
        return postRepository.findMostCommented()
                .stream()
                .filter(p -> !Boolean.TRUE.equals(p.getIsDeleted()))
                .limit(10)
                .toList();
    }

    @Override
    public List<Post> getByTags(List<String> tags) {
        return tags.stream()
                .flatMap(tag -> postRepository.findByTags_Name(tag).stream())
                .filter(p -> !Boolean.TRUE.equals(p.getIsDeleted()))
                .distinct()
                .toList();
    }

    @Override
    public List<Post> getLikedPosts(Long userId) {
        return postRepository.findByLikedByUsers_Id(userId)
                .stream()
                .filter(p -> !Boolean.TRUE.equals(p.getIsDeleted()))
                .toList();
    }
}
