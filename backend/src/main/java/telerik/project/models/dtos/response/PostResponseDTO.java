package telerik.project.models.dtos.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class PostResponseDTO {
    private Long id;

    private String title;
    private String content;

    private UserSummaryDTO author;

    private int likesCount;
    private boolean likedByCurrentUser;
    private Set<String> tags;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
