package telerik.project.controllers.rest;

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
import telerik.project.services.contracts.PostService;
import telerik.project.services.contracts.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/users")
public class UserRestController {

    private final UserService userService;
    private final PostService postService;
    private final UserMapper userMapper;
    private final PostMapper postMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO create(@RequestBody UserCreateDTO dto) {
        User user = userMapper.fromCreateDTO(dto);
        userService.create(user);
        return userMapper.toResponse(user);
    }

    @GetMapping("/{id}")
    public UserResponseDTO getById(@PathVariable Long id) {
        return userMapper.toResponse(userService.getById(id));
    }

    @PutMapping("/{id}")
    public UserResponseDTO update(@PathVariable Long id,
                                  @RequestParam Long actingUserId,
                                  @RequestBody UserUpdateDTO dto) {
        User actingUser = userService.getById(actingUserId);

        User newUser = new User();
        userMapper.updateUser(newUser, dto);

        userService.update(id, newUser, actingUser);

        return userMapper.toResponse(userService.getById(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id,
                        @RequestParam Long actingUserId) {
        User actingUser = userService.getById(actingUserId);
        userService.delete(id, actingUser);
    }

    @PostMapping("/{id}/follow")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void follow(@PathVariable Long id,
                       @RequestParam Long actingUserId) {
        User actingUser = userService.getById(actingUserId);
        userService.followUser(id, actingUser);
    }

    @DeleteMapping("/{id}/follow")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfollow(@PathVariable Long id,
                       @RequestParam Long actingUserId) {
        User actingUser = userService.getById(actingUserId);
        userService.unfollowUser(id, actingUser);
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

    @GetMapping
    public List<UserResponseDTO> search(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        UserFilterOptions filterOptions = new UserFilterOptions(
                username,
                firstName,
                lastName,
                email,
                false,
                sortBy, sortOrder,
                page, size
        );

        return userService.getAll(filterOptions).stream()
                .map(userMapper::toResponse)
                .toList();
    }
}
