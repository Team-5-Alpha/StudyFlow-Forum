package telerik.project.controllers.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import telerik.project.helpers.mappers.PostMapper;
import telerik.project.helpers.mappers.UserMapper;
import telerik.project.models.User;
import telerik.project.models.dtos.create.UserCreateDTO;
import telerik.project.models.dtos.response.PostResponseDTO;
import telerik.project.models.dtos.response.UserResponseDTO;
import telerik.project.models.dtos.response.UserSummaryDTO;
import telerik.project.models.dtos.update.UserUpdateDTO;
import telerik.project.models.filters.UserFilterOptions;
import telerik.project.services.contracts.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/users")
public class UserRestController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final PostMapper postMapper;

    @GetMapping
    public List<UserResponseDTO> search(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        UserFilterOptions filterOptions = new UserFilterOptions(
                username, firstName, lastName, email,
                false, sortBy, sortOrder, page, size);

        return userService.getAll(filterOptions).stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public UserResponseDTO getById(@PathVariable Long id) {
        return userMapper.toResponse(userService.getById(id));
    }

    @GetMapping("/{id}/followers")
    public List<UserSummaryDTO> getFollowers(@PathVariable Long id) {
        return userService.getFollowers(id).stream()
                .map(userMapper::toSummary)
                .toList();
    }

    @GetMapping("/{id}/following")
    public List<UserSummaryDTO> getFollowing(@PathVariable Long id) {
        return userService.getFollowing(id).stream()
                .map(userMapper::toSummary)
                .toList();
    }

    @GetMapping("/{id}/posts")
    public List<PostResponseDTO> getPosts(@PathVariable Long id) {
        return userService.getPostsByUser(id).stream()
                .map(postMapper::toResponse)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO create(@Valid @RequestBody UserCreateDTO dto) {
        User user = userMapper.fromCreateDTO(dto);
        userService.create(user);
        return userMapper.toResponse(user);
    }

    @PutMapping("/{id}")
    public UserResponseDTO update(
            @RequestHeader("X-User-Id") Long actingUserId,
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO dto
    ) {
        User actingUser = userService.getById(actingUserId);
        User target = userService.getById(id);

        userMapper.updateUser(target, dto);
        userService.update(id, target, actingUser);

        return userMapper.toResponse(userService.getById(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @RequestHeader("X-User-Id") Long actingUserId,
            @PathVariable Long id
    ) {
        User actingUser = userService.getById(actingUserId);
        userService.delete(id, actingUser);
    }

    @PostMapping("/{id}/follow")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void follow(
            @RequestHeader("X-User-Id") Long actingUserId,
            @PathVariable Long id
    ) {
        User actingUser = userService.getById(actingUserId);
        userService.followUser(id, actingUser);
    }

    @DeleteMapping("/{id}/follow")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfollow(
            @RequestHeader("X-User-Id") Long actingUserId,
            @PathVariable Long id
    ) {
        User actingUser = userService.getById(actingUserId);
        userService.unfollowUser(id, actingUser);
    }
}
