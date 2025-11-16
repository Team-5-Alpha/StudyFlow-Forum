package telerik.project.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import telerik.project.exceptions.AuthorizationException;
import telerik.project.exceptions.EntityNotFoundException;
import telerik.project.models.Comment;
import telerik.project.models.Post;
import telerik.project.models.Role;
import telerik.project.models.User;
import telerik.project.models.filters.CommentFilterOptions;
import telerik.project.repositories.CommentRepository;
import telerik.project.repositories.PostRepository;
import telerik.project.repositories.UserRepository;
import telerik.project.repositories.specifications.CommentSpecifications;
import telerik.project.services.contracts.CommentService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository,
                              PostRepository postRepository,
                              UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
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
    @Transactional
    public void create(Comment comment, Long postId, User author) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post", postId));

        User managedAuthor = userRepository.findById(author.getId())
                .orElseThrow(() -> new EntityNotFoundException("User", author.getId()));

        if (managedAuthor.getIsBlocked() != null && managedAuthor.getIsBlocked()) {
            throw new AuthorizationException("Blocked users cannot create comments.");
        }

        comment.setId(null);
        comment.setPost(post);
        comment.setAuthor(managedAuthor);
        if (comment.getIsDeleted() == null) {
            comment.setIsDeleted(false);
        }

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
    }

    @Override
    @Transactional
    public void update(Long id, Comment updatedComment, User actingUser) {
        Comment existing = getById(id);

        if (cannotModifyComment(existing, actingUser)) {
            throw new AuthorizationException("You are not allowed to modify this comment.");
        }

        existing.setContent(updatedComment.getContent());
        commentRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id, User actingUser) {
        Comment existing = getById(id);

        if (cannotModifyComment(existing, actingUser)) {
            throw new AuthorizationException("You are not allowed to delete this comment.");
        }

        existing.setIsDeleted(true);
        commentRepository.save(existing);
    }

    @Override
    @Transactional
    public void likeComment(Long commentId, User user) {
        Comment comment = getById(commentId);
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException("User", user.getId()));

        Set<User> likedByUsers = comment.getLikedByUsers();
        if (likedByUsers == null) {
            likedByUsers = new HashSet<>();
            comment.setLikedByUsers(likedByUsers);
        }

        if (managedUser.getLikedComments() == null) {
            managedUser.setLikedComments(new HashSet<>());
        }

        if (!likedByUsers.contains(managedUser)) {
            likedByUsers.add(managedUser);
            managedUser.getLikedComments().add(comment);
        }

        commentRepository.save(comment);
        userRepository.save(managedUser);
    }

    @Override
    @Transactional
    public void unlikeComment(Long commentId, User user) {
        Comment comment = getById(commentId);
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("User", user.getId()));

        Set<User> likedByUsers = comment.getLikedByUsers();
        if (likedByUsers != null) {
            likedByUsers.remove(managedUser);
        }

        if (managedUser.getLikedComments() != null) {
            managedUser.getLikedComments().remove(comment);
        }

        commentRepository.save(comment);
        userRepository.save(managedUser);
    }

    @Override
    public List<Comment> getReplies(Long parentCommentId) {
        return commentRepository.findByParentCommentId(parentCommentId);
    }

    @Override
    public long countByPostId(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    private boolean cannotModifyComment(Comment comment, User actingUser) {
        if (actingUser == null || actingUser.getId() == null) {
            return true;
        }

        boolean isBlocked = Boolean.TRUE.equals(actingUser.getIsBlocked());
        if (isBlocked) {
            return true;
        }

        boolean isAuthor = comment.getAuthor() != null
                && comment.getAuthor().getId().equals(actingUser.getId());

        boolean isAdmin = actingUser.getRole() == Role.ADMIN;

        return !(isAuthor || isAdmin);
    }
}