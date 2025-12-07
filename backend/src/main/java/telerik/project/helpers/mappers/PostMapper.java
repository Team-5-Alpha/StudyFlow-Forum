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
        return toResponse(post, null);
    }

    public PostResponseDTO toResponse(Post post, Long actingUserId) {
        PostResponseDTO dto = new PostResponseDTO();

        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());

        dto.setAuthor(userMapper.toSummary(post.getAuthor(), actingUserId));

        dto.setLikesCount(post.getLikedByUsers().size());

        boolean liked = false;
        if (actingUserId != null) {
            liked = post.getLikedByUsers()
                    .stream()
                    .anyMatch(u -> u.getId().equals(actingUserId));
        }
        dto.setLikedByCurrentUser(liked);

        dto.setTags(tagMapper.toNameSet(post.getTags()));

        return dto;
    }
}