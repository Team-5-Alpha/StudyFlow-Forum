package telerik.project.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import telerik.project.exceptions.EntityNotFoundException;
import telerik.project.helpers.AuthorizationHelper;
import telerik.project.helpers.validators.ActionValidationHelper;
import telerik.project.helpers.validators.UserValidationHelper;
import telerik.project.models.Post;
import telerik.project.models.Role;
import telerik.project.models.User;
import telerik.project.models.filters.UserFilterOptions;
import telerik.project.repositories.UserRepository;
import telerik.project.repositories.specifications.UserSpecifications;
import telerik.project.security.auth.SecurityContextUtil;
import telerik.project.services.contracts.NotificationService;
import telerik.project.services.contracts.PostService;
import telerik.project.services.contracts.UserService;
import telerik.project.utils.NormalizationUtils;
import telerik.project.utils.PaginationUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PostService postService;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<User> getAll(UserFilterOptions filterOptions) {
        Pageable pageable = PaginationUtils.createPageable(
                filterOptions.getPage(),
                filterOptions.getSize(),
                UserSpecifications.buildSort(filterOptions)
        );

        return userRepository
                .findAll(UserSpecifications.withFilters(filterOptions), pageable)
                .getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public User getById(Long targetUserId) {
        return userRepository.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("User", targetUserId));
    }

    @Override
    @Transactional(readOnly = true)
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User", "username", username));
    }

    @Override
    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User", "email", email));
    }

    @Override
    @Transactional
    public void create(User user) {
        String normalizedEmail = NormalizationUtils.normalizeEmail(user.getEmail());
        String normalizedUsername = NormalizationUtils.normalizeUsername(user.getUsername());

        UserValidationHelper.validateUsernameNotTaken(userRepository, normalizedUsername);
        UserValidationHelper.validateEmailNotTaken(userRepository, normalizedEmail);

        user.setEmail(normalizedEmail);
        user.setUsername(normalizedUsername);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void update(Long targetUserId, User updatedUser) {
        AuthorizationHelper.validateNotBlocked();

        User targetUser = getById(targetUserId);
        AuthorizationHelper.validateOwner(targetUser);

        String normalizedEmail = NormalizationUtils.normalizeEmail(updatedUser.getEmail());
        UserValidationHelper.validateEmailAvailable(userRepository, normalizedEmail, targetUser.getEmail());

        targetUser.setFirstName(updatedUser.getFirstName());
        targetUser.setLastName(updatedUser.getLastName());
        targetUser.setEmail(normalizedEmail);
        targetUser.setProfilePhotoURL(updatedUser.getProfilePhotoURL());
        targetUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));

        if (targetUser.isAdmin()) {
            targetUser.setPhoneNumber(updatedUser.getPhoneNumber());
        }

        userRepository.save(targetUser);
    }

    @Override
    @Transactional
    public void delete(Long targetUserId) {
        User user = getById(targetUserId);
        AuthorizationHelper.validateOwnerOrAdmin(user);
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void blockUser(Long targetUserId) {
        AuthorizationHelper.validateAdmin();
        AuthorizationHelper.validateSelfOperationNotAllowed(targetUserId);

        User targetUser = getById(targetUserId);
        ActionValidationHelper.validateCanBlock(targetUser);

        targetUser.setIsBlocked(true);
        userRepository.save(targetUser);

        notificationService.send(
                targetUser,
                targetUserId,
                "USER",
                "BLOCK"
        );
    }

    @Override
    @Transactional
    public void unblockUser(Long targetUserId) {
        AuthorizationHelper.validateAdmin();
        AuthorizationHelper.validateSelfOperationNotAllowed(targetUserId);

        User target = getById(targetUserId);
        ActionValidationHelper.validateCanUnblock(target);

        target.setIsBlocked(false);
        userRepository.save(target);

        notificationService.send(
                target,
                targetUserId,
                "USER",
                "UNBLOCK"
        );
    }

    @Override
    @Transactional
    public void promoteToAdmin(Long targetUserId) {
        AuthorizationHelper.validateAdmin();
        AuthorizationHelper.validateSelfOperationNotAllowed(targetUserId);

        User target = getById(targetUserId);
        ActionValidationHelper.validateCanPromote(target);

        target.setRole(Role.ADMIN);
        userRepository.save(target);

        notificationService.send(
                target,
                targetUserId,
                "ADMIN",
                "PROMOTE"
        );
    }

    @Override
    @Transactional
    public List<Post> getPostsByUser(Long targetUserId) {
        return postService.getByAuthorId(targetUserId);
    }

    @Override
    @Transactional
    public void followUser(Long targetUserId) {
        AuthorizationHelper.validateNotBlocked();
        AuthorizationHelper.validateSelfOperationNotAllowed(targetUserId);

        User target = getById(targetUserId);
        ActionValidationHelper.validateCanFollow(target);

        User actingUser = SecurityContextUtil.getCurrentUser();
        actingUser.getFollowing().add(target);
        userRepository.save(actingUser);

        notificationService.send(
                target,
                targetUserId,
                "USER",
                "FOLLOW"
        );
    }

    @Override
    @Transactional
    public void unfollowUser(Long targetUserId) {
        AuthorizationHelper.validateNotBlocked();
        AuthorizationHelper.validateSelfOperationNotAllowed(targetUserId);

        User target = getById(targetUserId);
        ActionValidationHelper.validateCanUnfollow(target);

        User actingUser = SecurityContextUtil.getCurrentUser();
        actingUser.getFollowing().remove(target);
        userRepository.save(actingUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getFollowers(Long targetUserId) {
        return List.copyOf(getById(targetUserId).getFollowers());
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getFollowing(Long targetUserId) {
        return List.copyOf(getById(targetUserId).getFollowing());
    }

    @Override
    @Transactional(readOnly = true)
    public long countFollowers(Long targetUserId) {
        return getById(targetUserId).getFollowers().size();
    }

    @Override
    @Transactional(readOnly = true)
    public long countFollowing(Long targetUserId) {
        return getById(targetUserId).getFollowing().size();
    }
}
