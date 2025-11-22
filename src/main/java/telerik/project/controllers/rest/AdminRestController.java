package telerik.project.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import telerik.project.helpers.mappers.UserMapper;
import telerik.project.models.User;
import telerik.project.models.dtos.response.AdminResponseDTO;
import telerik.project.models.dtos.response.AdminUserResponseDTO;
import telerik.project.models.dtos.update.AdminUpdateDTO;
import telerik.project.models.filters.UserFilterOptions;
import telerik.project.services.contracts.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin")
public class AdminRestController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PutMapping("/{id}/block")
    public AdminUserResponseDTO blockUser(@PathVariable Long id,
                                          @RequestParam Long actingUserId) {
        User actingUser = userService.getById(actingUserId);
        userService.blockUser(id, actingUser);

        return userMapper.toAdminUserResponse(userService.getById(id));
    }

    @PutMapping("/{id}/unblock")
    public AdminUserResponseDTO unblockUser(@PathVariable Long id,
                                            @RequestParam Long actingUserId) {
        User actingUser = userService.getById(actingUserId);
        userService.unblockUser(id, actingUser);

        return userMapper.toAdminUserResponse(userService.getById(id));
    }

    @PutMapping("/{id}/promote")
    public AdminUserResponseDTO promoteUser(@PathVariable Long id,
                                            @RequestParam Long actingUserId) {
        User actingUser = userService.getById(actingUserId);
        userService.promoteToAdmin(id, actingUser);

        return userMapper.toAdminUserResponse(userService.getById(id));
    }

    @PutMapping
    public AdminResponseDTO update(@PathVariable Long id,
                                   @RequestParam Long actingUserId,
                                   @RequestBody AdminUpdateDTO dto) {
        User acting = userService.getById(actingUserId);
        User target = userService.getById(id);

        userMapper.updateAdmin(target, dto);
        userService.update(id, target, acting);

        return userMapper.toAdminResponse(target);
    }

    @GetMapping("/users")
    public List<AdminUserResponseDTO> search(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean isBlocked,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        UserFilterOptions filterOptions = new UserFilterOptions(
                username,
                firstName,
                lastName,
                email,
                isBlocked,
                sortBy, sortOrder,
                page, size
        );

        return userService.getAll(filterOptions).stream()
                .map(userMapper::toAdminUserResponse)
                .toList();
    }
}
