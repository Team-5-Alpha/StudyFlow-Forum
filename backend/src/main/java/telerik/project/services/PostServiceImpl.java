package telerik.project.services;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import telerik.project.exceptions.EntityNotFoundException;
import telerik.project.helpers.AuthorizationHelper;
import telerik.project.helpers.validators.ActionValidationHelper;
import telerik.project.helpers.validators.PostValidationHelper;
import telerik.project.models.Post;
import telerik.project.models.Tag;
import telerik.project.models.User;
import telerik.project.models.dtos.create.PostCreateDTO;
import telerik.project.models.dtos.update.PostUpdateDTO;
import telerik.project.models.filters.PostFilterOptions;
import telerik.project.repositories.PostRepository;
import telerik.project.repositories.UserRepository;
import telerik.project.repositories.specifications.PostSpecifications;
import telerik.project.security.auth.SecurityContextUtil;
import telerik.project.services.contracts.NotificationService;
import telerik.project.services.contracts.PostService;
import telerik.project.services.contracts.TagService;
import telerik.project.utils.NormalizationUtils;
import telerik.project.utils.PaginationUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final TagService tagService;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public PostServiceImpl(PostRepository postRepository,
                           TagService tagService,
                           NotificationService notificationService,
                           UserRepository userRepository) {
        this.postRepository = postRepository;
        this.tagService = tagService;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getAll(PostFilterOptions filterOptions) {
        Pageable pageable = PaginationUtils.createPageable(
                filterOptions.getPage(),
                filterOptions.getSize(),
                PostSpecifications.buildSort(filterOptions)
        );

        return postRepository
                .findAll(PostSpecifications.withFilters(filterOptions), pageable)
                .getContent().stream()
                .filter(p -> !p.isDeleted())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Post getById(Long targetPostId) {
        Post post = postRepository.findById(targetPostId)
                .orElseThrow(() -> new EntityNotFoundException("Post", targetPostId));

        PostValidationHelper.validateNotDeleted(post);
        return post;
    }

    @Override
    @Transactional
    public Post create(PostCreateDTO dto) {
        User actingUser = SecurityContextUtil.getCurrentUser();
        AuthorizationHelper.validateNotBlocked();

        Post post = new Post();
        post.setAuthor(actingUser);
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        postRepository.save(post);

        actingUser.getFollowers().forEach(follower ->
                notificationService.send(
                        follower,
                        post.getId(),
                        "POST",
                        "CREATE"
                )
        );

        return post;
    }

    @Override
    @Transactional
    public void update(Long targetPostId, PostUpdateDTO dto) {
        AuthorizationHelper.validateNotBlocked();

        Post existing = getById(targetPostId);
        PostValidationHelper.validateNotDeleted(existing);
        AuthorizationHelper.validateOwner(existing.getAuthor());

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            existing.setTitle(dto.getTitle());
        }

        if (dto.getContent() != null && !dto.getContent().isBlank()) {
            existing.setContent(dto.getContent());
        }

        if (dto.getTags() != null) {
            Set<Tag> resolved = dto.getTags().stream()
                    .map(NormalizationUtils::normalizeTagName)
                    .map(tagService::createIfNotExists)
                    .collect(Collectors.toSet());

            existing.setTags(resolved);
        }

        postRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long targetPostId) {
        AuthorizationHelper.validateNotBlocked();

        Post post = getById(targetPostId);
        PostValidationHelper.validateNotDeleted(post);
        AuthorizationHelper.validateOwnerOrAdmin(post.getAuthor());

        post.setIsDeleted(true);
        postRepository.save(post);

        if (SecurityContextUtil.getCurrentUser().isAdmin()) {
            notificationService.send(
                    post.getAuthor(),
                    targetPostId,
                    "POST",
                    "DELETED"
            );
        }
    }

    @Override
    @Transactional
    public void likePost(Long targetPostId) {
        User actingUser = SecurityContextUtil.getCurrentUser();
        AuthorizationHelper.validateNotBlocked();

        Post post = getById(targetPostId);
        PostValidationHelper.validateNotDeleted(post);
        ActionValidationHelper.validateCanLike(post);

        actingUser.getLikedPosts().add(post);
        post.getLikedByUsers().add(actingUser);

        userRepository.save(actingUser);

        notificationService.send(
                post.getAuthor(),
                targetPostId,
                "POST",
                "LIKE"
        );
    }

    @Override
    @Transactional
    public void unlikePost(Long targetPostId) {
        User actingUser = SecurityContextUtil.getCurrentUser();
        AuthorizationHelper.validateNotBlocked();

        Post post = getById(targetPostId);
        PostValidationHelper.validateNotDeleted(post);
        ActionValidationHelper.validateCanUnlike(post);

        actingUser.getLikedPosts().remove(post);
        post.getLikedByUsers().remove(actingUser);

        userRepository.save(actingUser);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByAuthor(Long targetUserId) {
        return postRepository.countByAuthor_Id(targetUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getByAuthorId(Long targetUserId) {
        return postRepository.findByAuthor_Id(targetUserId).stream()
                .filter(p -> !p.isDeleted())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getMostCommented() {
        return postRepository.findMostCommented()
                .stream()
                .filter(p -> !p.isDeleted())
                .limit(10)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getLikedPosts(Long targetUserId) {
        return postRepository.findByLikedByUsers_Id(targetUserId)
                .stream()
                .filter(p -> !p.isDeleted())
                .toList();
    }
}
