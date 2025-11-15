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

    // CRUD

    @Override
    public List<Comment> getAll(CommentFilterOptions filterOptions) {
        Sort sort = CommentSpecifications.buildSort(filterOptions);
        return commentRepository.findAll(
                CommentSpecifications.withFilters(filterOptions),
                sort
        );
    }

    @Override
    public Comment getById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Comment", id));
    }

    @Override
    @Transactional
    public void create(Comment comment, Long postId, User author) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Post", postId));

        // Ensure author is managed (optionally, depending on how you pass User)
        User managedAuthor = userRepository.findById(author.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException("User", author.getId()));

        comment.setId(null); // ensure new entity
        comment.setPost(post);
        comment.setAuthor(managedAuthor);
        if (comment.getIsDeleted() == null) {
            comment.setIsDeleted(false);
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
        // Do not allow changing author/post via update
        commentRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id, User actingUser) {
        Comment existing = getById(id);

        if (cannotModifyComment(existing, actingUser)) {
            throw new AuthorizationException("You are not allowed to delete this comment.");
        }

        // Soft delete
        existing.setIsDeleted(true);
        commentRepository.save(existing);
    }

    // Likes

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

        // Keep the owning side (User.likedComments) in sync if needed
        if (managedUser.getLikedComments() == null) {
            managedUser.setLikedComments(new HashSet<>());
        }

        if (!likedByUsers.contains(managedUser)) {
            likedByUsers.add(managedUser);
            managedUser.getLikedComments().add(comment);
        }
    }

    @Override
    @Transactional
    public void unlikeComment(Long commentId, User user) {
        Comment comment = getById(commentId);
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException("User", user.getId()));

        Set<User> likedByUsers = comment.getLikedByUsers();
        if (likedByUsers != null) {
            likedByUsers.remove(managedUser);
        }

        if (managedUser.getLikedComments() != null) {
            managedUser.getLikedComments().remove(comment);
        }
    }

    // Comment structure

    @Override
    public List<Comment> getReplies(Long parentCommentId) {
        return commentRepository.findByParentCommentId(parentCommentId);
    }

    @Override
    public long countByPostId(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    // Helpers

    private boolean cannotModifyComment(Comment comment, User actingUser) {
        if (actingUser == null || actingUser.getId() == null) {
            return true;
        }

        boolean isAuthor = comment.getAuthor() != null
                && comment.getAuthor().getId().equals(actingUser.getId());

        boolean isAdmin = actingUser.getRole() == Role.ADMIN;

        return !(isAuthor || isAdmin);
    }
}