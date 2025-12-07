package telerik.project.helpers.mappers;

import org.springframework.stereotype.Component;
import telerik.project.models.User;
import telerik.project.models.dtos.response.AdminResponseDTO;
import telerik.project.models.dtos.response.AdminUserResponseDTO;
import telerik.project.models.dtos.response.UserResponseDTO;
import telerik.project.models.dtos.response.UserSummaryDTO;
import telerik.project.models.dtos.update.AdminUpdateDTO;
import telerik.project.models.dtos.update.UserUpdateDTO;

@Component
public class UserMapper {

    public void updateUser(User user, UserUpdateDTO dto) {
        fillBaseUpdate(user, dto);
    }

    public void updateAdmin(User user, AdminUpdateDTO dto) {
        fillBaseUpdate(user, dto);
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isBlank()) {
            user.setPhoneNumber(dto.getPhoneNumber());
        }
    }

    public UserSummaryDTO toSummary(User user, Long actingUserId) {
        UserSummaryDTO dto = new UserSummaryDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setProfilePhotoUrl(user.getProfilePhotoURL());

        boolean isFollowed = false;
        if (actingUserId != null) {
            isFollowed = user.getFollowers()
                    .stream()
                    .anyMatch(u -> u.getId().equals(actingUserId));
        }

        dto.setFollowedByMe(isFollowed);

        return dto;
    }

    public UserResponseDTO toResponse(User user, Long actingUserId) {
        UserResponseDTO dto = new UserResponseDTO();
        fillBaseResponse(dto, user);

        dto.setPostsCount(user.getPosts().size());
        dto.setFollowersCount(user.getFollowers().size());
        dto.setFollowingCount(user.getFollowing().size());
        dto.setBlocked(user.getIsBlocked());

        boolean isFollowed = false;
        if (actingUserId != null) {
            isFollowed = user.getFollowers()
                    .stream()
                    .anyMatch(u -> u.getId().equals(actingUserId));
        }
        dto.setFollowedByMe(isFollowed);
        dto.setRole(user.getRole().name());

        return dto;
    }

    public AdminUserResponseDTO toAdminUserResponse(User user) {
        AdminUserResponseDTO dto = new AdminUserResponseDTO();
        fillBaseResponse(dto, user);
        dto.setBlocked(user.getIsBlocked());
        dto.setRole(user.getRole().name());
        return dto;
    }

    public AdminResponseDTO toAdminResponse(User user) {
        AdminResponseDTO dto = new AdminResponseDTO();
        fillBaseResponse(dto, user);
        dto.setPhoneNumber(user.getPhoneNumber());
        return dto;
    }

    private void fillBaseUpdate(User user, UserUpdateDTO dto) {

        if (dto.getFirstName() != null && !dto.getFirstName().isBlank()) {
            user.setFirstName(dto.getFirstName());
        }

        if (dto.getLastName() != null && !dto.getLastName().isBlank()) {
            user.setLastName(dto.getLastName());
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            user.setEmail(dto.getEmail());
        }

        if (dto.getProfilePhotoURL() != null && !dto.getProfilePhotoURL().isBlank()) {
            user.setProfilePhotoURL(dto.getProfilePhotoURL());
        }
    }

    public boolean changePassword(User user, UserUpdateDTO dto) {
        boolean isPasswordChanged = false;

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(dto.getPassword());
            isPasswordChanged = true;
        }

        return isPasswordChanged;
    }

    private void fillBaseResponse(UserResponseDTO dto, User user) {
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setProfilePhotoUrl(user.getProfilePhotoURL());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
    }
}