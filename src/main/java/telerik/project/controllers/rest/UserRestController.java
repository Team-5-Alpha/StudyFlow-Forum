package telerik.project.controllers.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import telerik.project.helpers.AuthorizationHelper;
import telerik.project.helpers.mappers.PostMapper;
import telerik.project.helpers.mappers.UserMapper;
import telerik.project.models.User;
import telerik.project.models.dtos.response.*;
import telerik.project.models.dtos.update.AdminUpdateDTO;
import telerik.project.models.dtos.update.UserUpdateDTO;
import telerik.project.models.filters.UserFilterOptions;
import telerik.project.services.contracts.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final PostMapper postMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<AdminUserResponseDTO> search(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean isBlocked,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        AuthorizationHelper.validateAdmin();
        UserFilterOptions filterOptions = new UserFilterOptions(
                username, firstName, lastName, email,
                isBlocked, sortBy, sortOrder, page, size
        );

        return userService.getAll(filterOptions).stream()
                .map(userMapper::toAdminUserResponse)
                .toList();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{targetUserId}")
    public UserResponseDTO getById(@PathVariable Long targetUserId) {
        return userMapper.toResponse(userService.getById(targetUserId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{targetUserId}/followers")
    public List<UserSummaryDTO> getFollowers(@PathVariable Long targetUserId) {
        return userService.getFollowers(targetUserId).stream()
                .map(userMapper::toSummary)
                .toList();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{targetUserId}/following")
    public List<UserSummaryDTO> getFollowing(@PathVariable Long targetUserId) {
        return userService.getFollowing(targetUserId).stream()
                .map(userMapper::toSummary)
                .toList();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{targetUserId}/posts")
    public List<PostResponseDTO> getPosts(@PathVariable Long targetUserId) {
        return userService.getPostsByUser(targetUserId).stream()
                .map(postMapper::toResponse)
                .toList();
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("update/{targetUserId}")
    public UserResponseDTO update(
            @PathVariable Long targetUserId,
            @Valid @RequestBody UserUpdateDTO dto
    ) {
        User updatedUser = userService.getById(targetUserId);

        userMapper.updateUser(updatedUser, dto);
        userService.update(targetUserId, updatedUser);

        return userMapper.toResponse(userService.getById(targetUserId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("update-admin/{targetUserId}")
    public AdminResponseDTO update(
            @PathVariable Long targetUserId,
            @Valid @RequestBody AdminUpdateDTO dto
    ) {
        User updatedUser = userService.getById(targetUserId);

        userMapper.updateAdmin(updatedUser, dto);
        userService.update(targetUserId, updatedUser);

        return userMapper.toAdminResponse(userService.getById(targetUserId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{targetUserId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long targetUserId) {
        userService.delete(targetUserId);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/{targetUserId}/follow")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void follow(@PathVariable Long targetUserId) {
        userService.followUser(targetUserId);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{targetUserId}/follow")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfollow(@PathVariable Long targetUserId) {
        userService.unfollowUser(targetUserId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{targetUserId}/block")
    public AdminUserResponseDTO blockUser(@PathVariable Long targetUserId) {
        userService.blockUser(targetUserId);
        return userMapper.toAdminUserResponse(userService.getById(targetUserId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{targetUserId}/unblock")
    public AdminUserResponseDTO unblockUser(@PathVariable Long targetUserId) {
        userService.unblockUser(targetUserId);
        return userMapper.toAdminUserResponse(userService.getById(targetUserId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{targetUserId}/promote")
    public AdminUserResponseDTO promoteUser(@PathVariable Long targetUserId) {
        userService.promoteToAdmin(targetUserId);
        return userMapper.toAdminUserResponse(userService.getById(targetUserId));
    }
}
