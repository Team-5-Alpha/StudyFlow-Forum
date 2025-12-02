package telerik.project.services;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import telerik.project.exceptions.EntityNotFoundException;
import telerik.project.helpers.AuthorizationHelper;
import telerik.project.helpers.validators.ActionValidationHelper;
import telerik.project.helpers.validators.CommentValidationHelper;
import telerik.project.helpers.validators.PostValidationHelper;
import telerik.project.models.Comment;
import telerik.project.models.Post;
import telerik.project.models.User;
import telerik.project.models.filters.CommentFilterOptions;
import telerik.project.repositories.CommentRepository;
import telerik.project.repositories.UserRepository;
import telerik.project.repositories.specifications.CommentSpecifications;
import telerik.project.security.auth.SecurityContextUtil;
import telerik.project.services.contracts.CommentService;
import telerik.project.services.contracts.NotificationService;
import telerik.project.services.contracts.PostService;
import telerik.project.utils.PaginationUtils;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository,
                              PostService postService,
                              NotificationService notificationService,
                              UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postService = postService;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getAll(CommentFilterOptions filterOptions) {
        Pageable pageable = PaginationUtils.createPageable(
                filterOptions.getPage(),
                filterOptions.getSize(),
                CommentSpecifications.buildSort(filterOptions)
        );

        return commentRepository
                .findAll(CommentSpecifications.withFilters(filterOptions), pageable)
                .getContent().stream()
                .filter(c -> !c.isDeleted())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Comment getById(Long targetCommentId) {
        Comment targetComment = commentRepository.findById(targetCommentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment", targetCommentId));

        CommentValidationHelper.validateNotDeleted(targetComment);
        return targetComment;
    }

    @Override
    @Transactional
    public void create(Comment comment, Long targetPostId) {
        User actingUser = SecurityContextUtil.getCurrentUser();
        AuthorizationHelper.validateNotBlocked();

        Post post = postService.getById(targetPostId);
        PostValidationHelper.validateNotDeleted(post);

        comment.setPost(post);
        comment.setAuthor(actingUser);

        if (comment.getParentComment() != null) {
            Comment parent = getById(comment.getParentComment().getId());

            CommentValidationHelper.validateParentNotDeleted(parent);
            CommentValidationHelper.validateReplySamePost(parent, targetPostId);

            comment.setParentComment(parent);

            notificationService.send(
                    parent.getAuthor(),
                    parent.getId(),
                    "COMMENT",
                    "REPLY"
            );
        }

        commentRepository.save(comment);
        notificationService.send(
                post.getAuthor(),
                targetPostId,
                "COMMENT",
                "CREATE"
        );
    }

    @Override
    @Transactional
    public void update(Long targetCommentId, Comment updatedComment) {
        AuthorizationHelper.validateNotBlocked();

        Comment existing = getById(targetCommentId);
        CommentValidationHelper.validateNotDeleted(existing);
        PostValidationHelper.validateNotDeleted(existing.getPost());

        AuthorizationHelper.validateOwner(existing.getAuthor());

        existing.setContent(updatedComment.getContent());
        commentRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long targetCommentId) {
        User actingUser = SecurityContextUtil.getCurrentUser();
        AuthorizationHelper.validateNotBlocked();

        Comment targetComment = getById(targetCommentId);
        CommentValidationHelper.validateNotDeleted(targetComment);
        AuthorizationHelper.validateOwnerOrAdmin(targetComment.getAuthor());

        targetComment.setIsDeleted(true);
        commentRepository.save(targetComment);

        if (actingUser.isAdmin()) {
            notificationService.send(
                    targetComment.getAuthor(),
                    targetCommentId,
                    "COMMENT",
                    "DELETED"
            );
        }
    }

    @Override
    @Transactional
    public void likeComment(Long targetCommentId) {
        User actingUser = SecurityContextUtil.getCurrentUser();
        AuthorizationHelper.validateNotBlocked();

        Comment targetComment = getById(targetCommentId);
        CommentValidationHelper.validateNotDeleted(targetComment);
        ActionValidationHelper.validateCanLike(targetComment);

        actingUser.getLikedComments().add(targetComment);
        targetComment.getLikedByUsers().add(actingUser);

        userRepository.save(actingUser);

        notificationService.send(
                targetComment.getAuthor(),
                targetCommentId,
                "COMMENT",
                "LIKE"
        );
    }

    @Override
    @Transactional
    public void unlikeComment(Long targetCommentId) {
        User actingUser = SecurityContextUtil.getCurrentUser();
        AuthorizationHelper.validateNotBlocked();

        Comment targetComment = getById(targetCommentId);
        CommentValidationHelper.validateNotDeleted(targetComment);
        ActionValidationHelper.validateCanUnlike(targetComment);

        actingUser.getLikedComments().remove(targetComment);
        targetComment.getLikedByUsers().remove(actingUser);

        userRepository.save(actingUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getReplies(Long parentCommentId) {
        List<Comment> replies = commentRepository.findByParentCommentId(parentCommentId);
        return replies.stream()
                .filter(c -> !c.isDeleted())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long countByPostId(Long postId) {
        return commentRepository.countByPostId(postId);
    }
}