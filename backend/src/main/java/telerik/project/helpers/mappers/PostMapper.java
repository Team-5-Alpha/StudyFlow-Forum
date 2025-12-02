package telerik.project.helpers.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import telerik.project.models.Post;
import telerik.project.models.dtos.response.PostResponseDTO;

@Component
@RequiredArgsConstructor
public class PostMapper {
    private final UserMapper userMapper;
    private final TagMapper tagMapper;

    public PostResponseDTO toResponse(Post post) {
        PostResponseDTO dto = new PostResponseDTO();

        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());

        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());

        dto.setAuthor(userMapper.toSummary(post.getAuthor()));
        dto.setLikesCount(post.getLikedByUsers().size());
        dto.setTags(tagMapper.toNameSet(post.getTags()));
        return dto;
    }
}
