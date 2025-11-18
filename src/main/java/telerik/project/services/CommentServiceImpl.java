package telerik.project.services;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import telerik.project.exceptions.EntityNotFoundException;
import telerik.project.helpers.AuthorizationHelper;
import telerik.project.helpers.validators.CommentValidationHelper;
import telerik.project.helpers.validators.PostValidationHelper;
import telerik.project.models.Comment;
import telerik.project.models.Post;
import telerik.project.models.User;
import telerik.project.models.filters.CommentFilterOptions;
import telerik.project.repositories.CommentRepository;
import telerik.project.repositories.specifications.CommentSpecifications;
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

    public CommentServiceImpl(CommentRepository commentRepository,
                              PostService postService,
                              NotificationService notificationService) {
        this.commentRepository = commentRepository;
        this.postService = postService;
        this.notificationService = notificationService;
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
                .getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Comment getById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment", id));

        CommentValidationHelper.validateNotDeleted(comment);

        return comment;
    }

    @Override
    @Transactional
    public void create(Comment comment, Long postId, User author) {
        AuthorizationHelper.validateNotBlocked(author);

        Post post = postService.getById(postId);
        PostValidationHelper.validateNotDeleted(post);

        comment.setPost(post);
        comment.setAuthor(author);

        if (comment.getParentComment() != null) {
            Comment parent = getById(comment.getParentComment().getId());

            CommentValidationHelper.validateParentNotDeleted(parent);
            CommentValidationHelper.validateReplySamePost(parent, postId);

            comment.setParentComment(parent);

            notificationService.send(
                    author,
                    parent.getAuthor(),
                    parent.getId(),
                    "COMMENT",
                    "REPLY"
            );
        }

        commentRepository.save(comment);

        notificationService.send(
                author,
                post.getAuthor(),
                postId,
                "COMMENT",
                "REPLY"
        );
    }

    @Override
    @Transactional
    public void update(Long commentId, Comment updatedComment, User actingUser) {
        AuthorizationHelper.validateNotBlocked(actingUser);

        Comment existing = getById(commentId);

        AuthorizationHelper.validateOwner(actingUser, existing.getAuthor());


        existing.setContent(updatedComment.getContent());
        commentRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long commentId, User actingUser) {
        AuthorizationHelper.validateNotBlocked(actingUser);

        Comment comment = getById(commentId);

        AuthorizationHelper.validateOwnerOrAdmin(actingUser, comment.getAuthor());

        comment.setIsDeleted(true);
        commentRepository.save(comment);

        if (actingUser.isAdmin()) {
            notificationService.send(
                    actingUser,
                    comment.getAuthor(),
                    commentId,
                    "COMMENT",
                    "DELETED"
            );
        }
    }

    @Override
    @Transactional
    public void likeComment(Long commentId, User user) {
        Comment comment = getById(commentId);

        if (!isLiked(comment, user)) {
            comment.getLikedByUsers().add(user);
            commentRepository.save(comment);

            notificationService.send(
                    user,
                    comment.getAuthor(),
                    commentId,
                    "COMMENT",
                    "LIKE"
            );
        }
    }

    @Override
    @Transactional
    public void unlikeComment(Long commentId, User user) {
        Comment comment = getById(commentId);

        if (isLiked(comment, user)) {
            comment.getLikedByUsers().remove(user);
            commentRepository.save(comment);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isLiked(Comment comment, User user) {
        return comment.getLikedByUsers().contains(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getReplies(Long parentCommentId) {
        return commentRepository.findByParentCommentId(parentCommentId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByPostId(Long postId) {
        return commentRepository.countByPostId(postId);
    }
}