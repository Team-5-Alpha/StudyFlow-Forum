package telerik.project.controllers.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import telerik.project.helpers.mappers.TagMapper;
import telerik.project.models.Tag;
import telerik.project.models.User;
import telerik.project.models.dtos.response.TagResponseDTO;
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
    public TagResponseDTO createTag(@Valid @RequestBody Tag tag) {
        Tag created = tagService.createIfNotExists(tag.getName());
        return tagMapper.toResponse(created);
    }

    @DeleteMapping("/{id}")
    public void deleteTag(@RequestParam Long actingUserId,
                          @PathVariable Long id) {
        User actingUser = userService.getById(actingUserId);
        tagService.delete(id, actingUser);
    }
}