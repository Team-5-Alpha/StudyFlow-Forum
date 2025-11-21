package telerik.project.helpers.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import telerik.project.models.Post;
import telerik.project.models.Tag;
import telerik.project.models.User;
import telerik.project.models.dtos.create.PostCreateDTO;
import telerik.project.models.dtos.response.PostResponseDTO;
import telerik.project.models.dtos.update.PostUpdateDTO;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostMapper {
    private final UserMapper userMapper;

    public Post fromCreateDTO(PostCreateDTO dto, User author) {
        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setAuthor(author);
        return post;
    }

    public void updatePost(Post post, PostUpdateDTO dto) {
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
    }

    public PostResponseDTO toResponse(Post post) {
        PostResponseDTO dto = new PostResponseDTO();

        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());

        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());

        dto.setAuthor(userMapper.toSummary(post.getAuthor()));
        dto.setLikesCount(post.getLikedByUsers().size());
        dto.setTags(post.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toSet()));
        return dto;
    }
}
