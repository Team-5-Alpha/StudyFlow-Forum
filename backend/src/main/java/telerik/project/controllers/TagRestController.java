package telerik.project.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import telerik.project.helpers.mappers.TagMapper;
import telerik.project.models.Tag;
import telerik.project.models.dtos.response.ResponseDTO;
import telerik.project.models.dtos.response.TagResponseDTO;
import telerik.project.models.dtos.update.TagUpdateDTO;
import telerik.project.services.contracts.TagService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TagRestController {

    private final TagService tagService;
    private final TagMapper tagMapper;

    @GetMapping("/private/tags")
    public ResponseDTO<List<TagResponseDTO>> getAllTags() {
        List<TagResponseDTO> data = tagService.getAll().stream()
                .map(tagMapper::toResponse)
                .toList();

        return ResponseDTO.success(data);
    }

    @GetMapping("/private/tags/{targetTagId}")
    public ResponseDTO<TagResponseDTO> getTagById(@PathVariable Long targetTagId) {
        TagResponseDTO dto = tagMapper.toResponse(tagService.getById(targetTagId));
        return ResponseDTO.success(dto);
    }

    @PostMapping("/admin/tags")
    public ResponseDTO<TagResponseDTO> createTag(@Valid @RequestBody Tag tag) {
        Tag created = tagService.createIfNotExists(tag.getName());
        TagResponseDTO dto = tagMapper.toResponse(created);
        return ResponseDTO.success("Tag created.", dto);
    }

    @PutMapping("/admin/tags/{targetTagId}")
    public ResponseDTO<TagResponseDTO> update(
            @PathVariable Long targetTagId,
            @Valid @RequestBody TagUpdateDTO dto
    ) {
        Tag updatedTag = tagService.getById(targetTagId);
        tagMapper.updateTag(updatedTag, dto);
        tagService.update(targetTagId, updatedTag);

        TagResponseDTO response = tagMapper.toResponse(tagService.getById(targetTagId));
        return ResponseDTO.success("Tag updated.", response);
    }

    @DeleteMapping("/admin/tags/{targetTagId}")
    public ResponseDTO<?> deleteTag(@PathVariable Long targetTagId) {
        tagService.delete(targetTagId);
        return ResponseDTO.success("Tag deleted.");
    }
}