package telerik.project.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import telerik.project.exceptions.AuthorizationException;
import telerik.project.exceptions.EntityDuplicateException;
import telerik.project.exceptions.EntityNotFoundException;
import telerik.project.models.Post;
import telerik.project.models.Role;
import telerik.project.models.User;
import telerik.project.models.filters.UserFilterOptions;
import telerik.project.repositories.UserRepository;
import telerik.project.repositories.specifications.UserSpecifications;
import telerik.project.services.contracts.NotificationService;
import telerik.project.services.contracts.PostService;
import telerik.project.services.contracts.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PostService postService;
    private final NotificationService notificationService;

    public UserServiceImpl(UserRepository userRepository, PostService postService, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.postService = postService;
        this.notificationService = notificationService;
    }

    @Override
    public List<User> getAll(UserFilterOptions filterOptions) {
        return userRepository.findAll(
                UserSpecifications.withFilters(filterOptions),
                UserSpecifications.buildSort(filterOptions)
        );
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id));
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User", "username", username));
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User", "email", email));
    }

    @Override
    public void create(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new EntityDuplicateException("User", "username", user.getUsername());
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EntityDuplicateException("User", "email", user.getEmail());
        }

        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        if (user.getIsBlocked() == null) {
            user.setIsBlocked(false);
        }

        // Todo: security phase - encode password
        userRepository.save(user);
    }

    @Override
    public void update(Long targetUserId, User updatedUser, User actingUser) {
        User target = getById(targetUserId);


        if (Boolean.TRUE.equals(target.getIsBlocked()) && !actingUser.isAdmin()) {
            throw new AuthorizationException("Blocked accounts cannot be modified.");
        }

        if (Boolean.TRUE.equals(actingUser.getIsBlocked())) {
            throw new AuthorizationException("Blocked accounts cannot modify profiles.");
        }

        if (!actingUser.isAdmin() && !target.getId().equals(actingUser.getId())) {
            throw new AuthorizationException("You are not allowed to modify this user.");
        }

        if (!target.getEmail().equals(updatedUser.getEmail())
                && userRepository.existsByEmail(updatedUser.getEmail())) {
            throw new EntityDuplicateException("User", "email", updatedUser.getEmail());
        }

        target.setFirstName(updatedUser.getFirstName());
        target.setLastName(updatedUser.getLastName());
        target.setEmail(updatedUser.getEmail());
        target.setPassword(updatedUser.getPassword()); //Todo: Security change later
        target.setPhoneNumber(updatedUser.getPhoneNumber());
        target.setProfilePhotoURL(updatedUser.getProfilePhotoURL());
        target.setUpdatedAt(LocalDateTime.now());

        userRepository.save(target);
    }

    @Override
    public void delete(Long targetUserId, User actingUser) {
        if (!actingUser.isAdmin()) {
            throw new AuthorizationException("Only admins can delete users.");
        }

        if (actingUser.getId().equals(targetUserId)) {
            throw new AuthorizationException("You cannot delete yourself.");
        }

        userRepository.delete(getById(targetUserId));
    }

    @Override
    public void blockUser(Long targetUserId, User actingUser) {
        if (!actingUser.isAdmin()) {
            throw new AuthorizationException("Only admins can block users.");
        }

        if (actingUser.getId().equals(targetUserId)) {
            throw new AuthorizationException("You cannot block yourself.");
        }

        User target = getById(targetUserId);
        target.setIsBlocked(true);
        userRepository.save(target);

        notificationService.send(actingUser, target, targetUserId, "USER", "BLOCK");
    }

    @Override
    public void unblockUser(Long targetUserId, User actingUser) {
        if (!actingUser.isAdmin()) {
            throw new AuthorizationException("Only admins can unblock users.");
        }

        if (actingUser.getId().equals(targetUserId)) {
            throw new AuthorizationException("You cannot unblock yourself.");
        }

        User target = getById(targetUserId);
        target.setIsBlocked(false);
        userRepository.save(target);

        notificationService.send(actingUser, target, targetUserId, "USER", "UNBLOCK");
    }

    @Override
    public void promoteToAdmin(Long targetUserId, User actingUser) {
        if (!actingUser.isAdmin()) {
            throw new AuthorizationException("Only admins can promote users.");
        }

        if (actingUser.getId().equals(targetUserId)) {
            throw new AuthorizationException("You cannot promote yourself.");
        }

        User target = getById(targetUserId);
        target.setRole(Role.ADMIN);
        userRepository.save(target);

        notificationService.send(actingUser, target, targetUserId, "ADMIN", "PROMOTE");
    }

    @Override
    public List<Post> getPostsByUser(Long userId) {
        return postService.getByAuthorId(userId);
    }

    @Override
    @Transactional
    public void followUser(Long targetUserId, User actingUser) {
        if (Boolean.TRUE.equals(actingUser.getIsBlocked())) {
            throw new AuthorizationException("Blocked users cannot follow others.");
        }

        if (actingUser.getId().equals(targetUserId)) {
            throw new AuthorizationException("You cannot follow yourself.");
        }

        User target = getById(targetUserId);

        if (!actingUser.getFollowing().contains(target)) {
            actingUser.getFollowing().add(target);
            userRepository.save(actingUser);

            notificationService.send(actingUser, target, targetUserId, "USER", "FOLLOW");
        }
    }

    @Override
    @Transactional
    public void unfollowUser(Long targetUserId, User actingUser) {
        if (Boolean.TRUE.equals(actingUser.getIsBlocked())) {
            throw new AuthorizationException("Blocked users cannot unfollow others.");
        }

        User target = getById(targetUserId);

        if (actingUser.getId().equals(targetUserId)) {
            throw new AuthorizationException("You cannot unfollow yourself.");
        }

        if (actingUser.getFollowing().contains(target)) {
            actingUser.getFollowing().remove(target);
            userRepository.save(actingUser);
        }
    }

    @Override
    public List<User> getFollowers(Long userId) {
        return getById(userId).getFollowers().stream().toList();
    }

    @Override
    public List<User> getFollowing(Long userId) {
        return getById(userId).getFollowing().stream().toList();
    }

    @Override
    public boolean isFollowing(Long userId, Long targetUserId) {
        return getById(userId).getFollowing().contains(getById(targetUserId));
    }

    @Override
    public long countFollowers(Long userId) {
        return getById(userId).getFollowers().size();
    }

    @Override
    public long countFollowing(Long userId) {
        return getById(userId).getFollowing().size();
    }

    @Override
    public void updateProfilePhoto(Long userId, String photoUrl, User actingUser) {
        if (Boolean.TRUE.equals(actingUser.getIsBlocked())) {
            throw new AuthorizationException("Blocked users cannot modify profile photos.");
        }

        User target = getById(userId);

        if(!actingUser.isAdmin() && !actingUser.getId().equals(target.getId())) {
            throw new AuthorizationException("You cannot modify another user's profile photo.");
        }

        target.setProfilePhotoURL(photoUrl);
        userRepository.save(target);
    }
}
