package telerik.project.helpers.mappers;

import org.springframework.stereotype.Component;
import telerik.project.models.User;
import telerik.project.models.dtos.create.UserCreateDTO;
import telerik.project.models.dtos.response.AdminResponseDTO;
import telerik.project.models.dtos.response.AdminUserResponseDTO;
import telerik.project.models.dtos.response.UserResponseDTO;
import telerik.project.models.dtos.response.UserSummaryDTO;
import telerik.project.models.dtos.update.AdminUpdateDTO;
import telerik.project.models.dtos.update.UserUpdateDTO;

@Component
public class UserMapper {

    public User fromCreateDTO(UserCreateDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        return user;
    }

    public void updateUser(User user, UserUpdateDTO dto) {
        fillBaseUpdate(dto, user);
    }

    public void updateAdmin(User user, AdminUpdateDTO dto) {
        fillBaseUpdate(dto, user);
        user.setPhoneNumber(dto.getPhoneNumber());
    }

    public UserSummaryDTO toSummary(User user) {
        UserSummaryDTO dto = new UserSummaryDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        return dto;
    }

    public UserResponseDTO toResponse(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        fillBaseResponse(dto, user);
        return dto;
    }

    public AdminUserResponseDTO toAdminUserResponse(User user) {
        AdminUserResponseDTO dto = new AdminUserResponseDTO();
        fillBaseResponse(dto, user);
        dto.setBlocked(user.getIsBlocked());
        return dto;
    }

    public AdminResponseDTO toAdminResponse(User user) {
        AdminResponseDTO dto = new AdminResponseDTO();
        fillBaseResponse(dto, user);
        dto.setPhoneNumber(user.getPhoneNumber());
        return dto;
    }

    private void fillBaseUpdate(UserUpdateDTO dto, User user) {
        if (dto.getFirstName() != null && !dto.getFirstName().isBlank()) {
            user.setFirstName(dto.getFirstName());
        }

        if (dto.getLastName() != null && !dto.getLastName().isBlank()) {
            user.setLastName(dto.getLastName());
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            user.setEmail(dto.getEmail());
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(dto.getPassword());
        }

        if (dto.getProfilePhotoURL() != null && !dto.getProfilePhotoURL().isBlank()) {
            user.setProfilePhotoURL(dto.getProfilePhotoURL());
        }
    }

    private void fillBaseResponse(UserResponseDTO dto, User user) {
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setProfilePhotoURL(user.getProfilePhotoURL());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
    }
}
