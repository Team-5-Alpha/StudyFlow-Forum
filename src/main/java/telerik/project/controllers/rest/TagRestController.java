package telerik.project.controllers.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import telerik.project.helpers.mappers.TagMapper;
import telerik.project.models.Tag;
import telerik.project.models.dtos.response.TagResponseDTO;
import telerik.project.models.dtos.update.TagUpdateDTO;
import telerik.project.services.contracts.TagService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagRestController {

    private final TagService tagService;
    private final TagMapper tagMapper;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public List<TagResponseDTO> getAllTags() {
        return tagService.getAll().stream()
                .map(tagMapper::toResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{targetTagId}")
    public TagResponseDTO getTagById(@PathVariable Long targetTagId) {
        return tagMapper.toResponse(tagService.getById(targetTagId));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TagResponseDTO createTag(@Valid @RequestBody Tag tag) {
        Tag created = tagService.createIfNotExists(tag.getName());
        return tagMapper.toResponse(created);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/{targetTagId}")
    public TagResponseDTO update(
            @PathVariable Long targetTagId,
            @Valid @RequestBody TagUpdateDTO dto
    ) {
        Tag updatedTag = tagService.getById(targetTagId);

        tagMapper.updateTag(updatedTag, dto);
        tagService.update(targetTagId, updatedTag);

        return tagMapper.toResponse(tagService.getById(targetTagId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{targetTagId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTag(@PathVariable Long targetTagId) {
        tagService.delete(targetTagId);
    }
}