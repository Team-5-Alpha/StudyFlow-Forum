package telerik.project.helpers.mappers;

import org.springframework.stereotype.Component;
import telerik.project.models.Tag;
import telerik.project.models.dtos.response.TagResponseDTO;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TagMapper {

    public TagResponseDTO toResponse(Tag tag) {
        TagResponseDTO dto = new TagResponseDTO();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        return dto;
    }

    public Set<String> toNameSet(Set<Tag> tags) {
        return tags.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());
    }

    public Set<TagResponseDTO> toResponseSet(Set<Tag> tags) {
        return tags.stream()
                .map(this::toResponse)
                .collect(Collectors.toSet());
    }
}
