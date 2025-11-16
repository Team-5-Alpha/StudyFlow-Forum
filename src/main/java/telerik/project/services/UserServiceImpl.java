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
import telerik.project.repositories.PostRepository;
import telerik.project.repositories.UserRepository;
import telerik.project.repositories.specifications.UserSpecifications;
import telerik.project.services.contracts.NotificationService;
import telerik.project.services.contracts.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;

    public UserServiceImpl(UserRepository userRepository,
                           PostRepository postRepository,
                           NotificationService notificationService) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
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

        //Todo: security phase - encode password
        userRepository.save(user);
    }

    @Override
    public void update(Long id, User updatedUser, User actingUser) {
        User existing = getById(id);

        if (!actingUser.isAdmin() && !existing.getId().equals(actingUser.getId())) {
            throw new AuthorizationException("You are not allowed to modify this user.");
        }

        if (!existing.getUsername().equals(updatedUser.getUsername())
                && userRepository.existsByUsername(updatedUser.getUsername())) {
            throw new EntityDuplicateException("User", "username", updatedUser.getUsername());
        }

        if (!existing.getEmail().equals(updatedUser.getEmail())
                && userRepository.existsByEmail(updatedUser.getEmail())) {
            throw new EntityDuplicateException("User", "email", updatedUser.getEmail());
        }

        existing.setFirstName(updatedUser.getFirstName());
        existing.setLastName(updatedUser.getLastName());
        existing.setEmail(updatedUser.getEmail());
        existing.setUsername(updatedUser.getUsername());
        existing.setPassword(updatedUser.getPassword()); //Todo: Security change later
        existing.setPhoneNumber(updatedUser.getPhoneNumber());
        existing.setProfilePhotoURL(updatedUser.getProfilePhotoURL());
        existing.setUpdatedAt(LocalDateTime.now());

        userRepository.save(existing);
    }

    @Override
    public void delete(Long id, User actingUser) {
        if (!actingUser.isAdmin()) {
            throw new AuthorizationException("Only admins can delete users.");
        }

        User user = getById(id);
        userRepository.delete(user);
    }

    @Override
    public void blockUser(Long id, User actingUser) {
        if (!actingUser.isAdmin()) {
            throw new AuthorizationException("Only admins can block users.");
        }

        if (actingUser.getId().equals(id)) {
            throw new AuthorizationException("You cannot block yourself.");
        }

        User user = getById(id);
        user.setIsBlocked(true);
        userRepository.save(user);

        //Todo: Notify blocked user
    }

    @Override
    public void unblockUser(Long id, User actingUser) {
        if (!actingUser.isAdmin()) {
            throw new AuthorizationException("Only admins can unblock users.");
        }

        if (actingUser.getId().equals(id)) {
            throw new AuthorizationException("You cannot unblock yourself.");
        }

        User target = getById(id);
        target.setIsBlocked(false);
        userRepository.save(target);

        //Todo: Notify unblocked user
    }

    @Override
    public void promoteToAdmin(Long id, User actingUser) {
        if (!actingUser.isAdmin()) {
            throw new AuthorizationException("Only admins can promote users.");
        }

        if (actingUser.getId().equals(id)) {
            throw new AuthorizationException("You cannot promote yourself.");
        }

        User target = getById(id);
        target.setRole(Role.ADMIN);
        userRepository.save(target);

        //Todo: Notify promoted user
    }

    @Override
    public List<Post> getPostsByUser(Long userId) {
        return postRepository.findByAuthor_Id(userId);
    }

    @Override
    @Transactional
    public void followUser(Long targetUserId, User actingUser) {
        User target = getById(targetUserId);

        if (actingUser.getId().equals(targetUserId)) {
            throw new AuthorizationException("You cannot follow yourself.");
        }

        if (actingUser.getFollowing().contains(target)) {
            return;
        }

        actingUser.getFollowing().add(target);
        userRepository.save(actingUser);

        //Todo: notify user
    }

    @Override
    @Transactional
    public void unfollowUser(Long targetUserId, User actingUser) {
        User target = getById(targetUserId);

        if (!actingUser.getFollowing().contains(target)) {
            return;
        }

        actingUser.getFollowing().remove(target);
        userRepository.save(actingUser);
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
        User user = getById(userId);
        User target = getById(targetUserId);

        return user.getFollowing().contains(target);
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
        User target = getById(userId);

        if(!actingUser.isAdmin() && !actingUser.getId().equals(target.getId())) {
            throw new AuthorizationException("You cannot modify another user's profile photo.");
        }

        target.setProfilePhotoURL(photoUrl);
        userRepository.save(target);
    }
}
