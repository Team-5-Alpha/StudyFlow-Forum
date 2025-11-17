package telerik.project.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import telerik.project.exceptions.AuthorizationException;
import telerik.project.exceptions.EntityNotFoundException;
import telerik.project.models.Comment;
import telerik.project.models.Post;
import telerik.project.models.User;
import telerik.project.models.filters.CommentFilterOptions;
import telerik.project.repositories.CommentRepository;
import telerik.project.repositories.specifications.CommentSpecifications;
import telerik.project.services.contracts.CommentService;
import telerik.project.services.contracts.NotificationService;
import telerik.project.services.contracts.PostService;

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
    public List<Comment> getAll(CommentFilterOptions filterOptions) {
        return commentRepository.findAll(
                CommentSpecifications.withFilters(filterOptions),
                CommentSpecifications.buildSort(filterOptions)
        );
    }

    @Override
    public Comment getById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment", id));
    }

    @Override
    public void create(Comment comment, Long postId, User author) {
        if (Boolean.TRUE.equals(author.getIsBlocked())) {
            throw new AuthorizationException("Blocked users cannot create comments.");
        }

        if (author.getIsBlocked()) {
            throw new AuthorizationException("Blocked users cannot create comments.");
        }

        Post post = postService.getById(postId);

        comment.setId(null);
        comment.setPost(post);
        comment.setAuthor(author);

        if (comment.getParentComment() != null) {
            Comment parent = getById(comment.getParentComment().getId());

            if (!parent.getPost().getId().equals(postId)) {
                throw new AuthorizationException("Reply must belong to the same post.");
            }

            if (Boolean.TRUE.equals(parent.getIsDeleted())) {
                throw new AuthorizationException("Cannot reply to a deleted comment.");
            }

            comment.setParentComment(parent);
        }

        commentRepository.save(comment);

        notificationService.send(author, post.getAuthor(), postId, "COMMENT", "CREATE");
    }

    @Override
    @Transactional
    public void update(Long id, Comment updatedComment, User actingUser) {
        if (Boolean.TRUE.equals(actingUser.getIsBlocked())) {
            throw new AuthorizationException("Blocked users cannot update comments.");
        }

        Comment existing = getById(id);

        if (!actingUser.isAdmin() && !existing.getAuthor().getId().equals(actingUser.getId())) {
            throw new AuthorizationException("You cannot modify this comment.");
        }

        existing.setContent(updatedComment.getContent());
        commentRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id, User actingUser) {
        if (Boolean.TRUE.equals(actingUser.getIsBlocked())) {
            throw new AuthorizationException("Blocked users cannot delete comments.");
        }
        Comment comment = getById(id);

        if (!actingUser.isAdmin() && !comment.getAuthor().getId().equals(actingUser.getId())) {
            throw new AuthorizationException("You cannot delete this comment.");
        }

        comment.setIsDeleted(true);
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void likeComment(Long commentId, User user) {
        Comment comment = getById(commentId);

        if (!comment.getLikedByUsers().contains(user)) {
            comment.getLikedByUsers().add(user);
            commentRepository.save(comment);

            notificationService.send(user, comment.getAuthor(), commentId, "COMMENT", "LIKE");
        }
    }

    @Override
    @Transactional
    public void unlikeComment(Long commentId, User user) {
        Comment comment = getById(commentId);

        if (comment.getLikedByUsers().contains(user)) {
            comment.getLikedByUsers().remove(user);
            commentRepository.save(comment);
        }
    }

    @Override
    public List<Comment> getReplies(Long parentCommentId) {
        return commentRepository.findByParentCommentId(parentCommentId);
    }

    @Override
    public long countByPostId(Long postId) {
        return commentRepository.countByPostId(postId);
    }
}