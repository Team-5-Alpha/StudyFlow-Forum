package telerik.project.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import telerik.project.helpers.mappers.PostMapper;
import telerik.project.helpers.mappers.UserMapper;
import telerik.project.models.User;
import telerik.project.models.dtos.response.ResponseDTO;
import telerik.project.models.dtos.response.*;
import telerik.project.models.dtos.update.AdminUpdateDTO;
import telerik.project.models.dtos.update.UserUpdateDTO;
import telerik.project.models.filters.UserFilterOptions;
import telerik.project.security.auth.SecurityContextUtil;
import telerik.project.services.contracts.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserRestController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final PostMapper postMapper;

    @GetMapping("/admin/users")
    public ResponseDTO<List<AdminUserResponseDTO>> search(
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
        UserFilterOptions filterOptions = new UserFilterOptions(
                username, firstName, lastName, email,
                isBlocked, sortBy, sortOrder, page, size
        );

        List<AdminUserResponseDTO> data = userService.getAll(filterOptions).stream()
                .map(userMapper::toAdminUserResponse)
                .toList();

        return ResponseDTO.success(data);
    }

    @GetMapping("/private/users/{targetUserId}")
    public ResponseDTO<UserResponseDTO> getById(@PathVariable Long targetUserId) {
        User user = userService.getById(targetUserId);
        Long actingUserId = SecurityContextUtil.getCurrentUserId();

        UserResponseDTO dto = userMapper.toResponse(user, actingUserId);
        return ResponseDTO.success(dto);
    }

    @GetMapping("/private/users/{targetUserId}/followers")
    public ResponseDTO<List<UserSummaryDTO>> getFollowers(@PathVariable Long targetUserId) {

        Long actingUserId = SecurityContextUtil.getCurrentUserId();

        List<UserSummaryDTO> data = userService.getFollowers(targetUserId).stream()
                .map(u -> userMapper.toSummary(u, actingUserId))
                .toList();

        return ResponseDTO.success(data);
    }

    @GetMapping("/private/users/{targetUserId}/following")
    public ResponseDTO<List<UserSummaryDTO>> getFollowing(@PathVariable Long targetUserId) {

        Long actingUserId = SecurityContextUtil.getCurrentUserId();

        List<UserSummaryDTO> data = userService.getFollowing(targetUserId).stream()
                .map(u -> userMapper.toSummary(u, actingUserId))
                .toList();

        return ResponseDTO.success(data);
    }

    @GetMapping("/private/users/{targetUserId}/posts")
    public ResponseDTO<List<PostResponseDTO>> getPosts(@PathVariable Long targetUserId) {
        List<PostResponseDTO> data = userService.getPostsByUser(targetUserId).stream()
                .map(postMapper::toResponse)
                .toList();

        return ResponseDTO.success(data);
    }

    @PutMapping("/private/users/{targetUserId}")
    public ResponseDTO<UserResponseDTO> update(
            @PathVariable Long targetUserId,
            @Valid @RequestBody UserUpdateDTO dto
    ) {
        User updatedUser = userService.getById(targetUserId);

        userMapper.updateUser(updatedUser, dto);
        boolean isPasswordChanged = userMapper.changePassword(updatedUser, dto);

        userService.update(targetUserId, updatedUser, isPasswordChanged);

        User refreshed = userService.getById(targetUserId);
        Long actingUserId = SecurityContextUtil.getCurrentUserId();
        UserResponseDTO response = userMapper.toResponse(refreshed, actingUserId);

        return ResponseDTO.success("User updated.", response);
    }

    @PutMapping("/admin/users/{targetUserId}")
    public ResponseDTO<AdminResponseDTO> updateAdmin(
            @PathVariable Long targetUserId,
            @Valid @RequestBody AdminUpdateDTO dto
    ) {
        User updatedUser = userService.getById(targetUserId);

        userMapper.updateAdmin(updatedUser, dto);
        boolean isPasswordChanged = userMapper.changePassword(updatedUser, dto);

        userService.update(targetUserId, updatedUser, isPasswordChanged);

        AdminResponseDTO response = userMapper.toAdminResponse(userService.getById(targetUserId));

        return ResponseDTO.success("User updated by admin.", response);
    }

    @DeleteMapping("/private/users/{targetUserId}")
    public ResponseDTO<?> delete(@PathVariable Long targetUserId) {
        userService.delete(targetUserId);
        return ResponseDTO.success("User deleted.");
    }

    @PostMapping("/private/users/{targetUserId}/follow")
    public ResponseDTO<?> follow(@PathVariable Long targetUserId) {
        userService.followUser(targetUserId);
        return ResponseDTO.success("User followed.");
    }

    @DeleteMapping("/private/users/{targetUserId}/follow")
    public ResponseDTO<?> unfollow(@PathVariable Long targetUserId) {
        userService.unfollowUser(targetUserId);
        return ResponseDTO.success("User unfollowed.");
    }

    @PutMapping("/admin/users/{targetUserId}/block")
    public ResponseDTO<AdminUserResponseDTO> blockUser(@PathVariable Long targetUserId) {
        userService.blockUser(targetUserId);
        AdminUserResponseDTO response =
                userMapper.toAdminUserResponse(userService.getById(targetUserId));
        return ResponseDTO.success("User blocked.", response);
    }

    @PutMapping("/admin/users/{targetUserId}/unblock")
    public ResponseDTO<AdminUserResponseDTO> unblockUser(@PathVariable Long targetUserId) {
        userService.unblockUser(targetUserId);
        AdminUserResponseDTO response =
                userMapper.toAdminUserResponse(userService.getById(targetUserId));
        return ResponseDTO.success("User unblocked.", response);
    }

    @PutMapping("/admin/users/{targetUserId}/promote")
    public ResponseDTO<AdminUserResponseDTO> promoteUser(@PathVariable Long targetUserId) {
        userService.promoteToAdmin(targetUserId);
        AdminUserResponseDTO response =
                userMapper.toAdminUserResponse(userService.getById(targetUserId));
        return ResponseDTO.success("User promoted to admin.", response);
    }
}