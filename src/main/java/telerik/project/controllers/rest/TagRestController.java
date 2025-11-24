package telerik.project.controllers.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import telerik.project.helpers.mappers.TagMapper;
import telerik.project.models.Tag;
import telerik.project.models.User;
import telerik.project.models.dtos.response.TagResponseDTO;
import telerik.project.models.dtos.update.TagUpdateDTO;
import telerik.project.services.contracts.TagService;
import telerik.project.services.contracts.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagRestController {

    private final TagService tagService;
    private final TagMapper tagMapper;
    private final UserService userService;

    @GetMapping
    public List<TagResponseDTO> getAllTags() {
        return tagService.getAll().stream()
                .map(tagMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public TagResponseDTO getTagById(@PathVariable Long id) {
        return tagMapper.toResponse(tagService.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TagResponseDTO createTag(@Valid @RequestBody Tag tag) {
        Tag created = tagService.createIfNotExists(tag.getName());
        return tagMapper.toResponse(created);
    }

    @PutMapping("/{id}")
    public TagResponseDTO update(
            @RequestHeader("X-User-Id") Long actingUserId,
            @PathVariable Long id,
            @Valid @RequestBody TagUpdateDTO dto
    ) {
        User actingUser = userService.getById(actingUserId);
        Tag updated = tagService.getById(id);

        tagMapper.updateTag(updated, dto);
        tagService.update(id, updated, actingUser);

        return tagMapper.toResponse(tagService.getById(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTag(
            @RequestHeader("X-User-Id") Long actingUserId,
            @PathVariable Long id
    ) {
        User actingUser = userService.getById(actingUserId);
        tagService.delete(id, actingUser);
    }
}