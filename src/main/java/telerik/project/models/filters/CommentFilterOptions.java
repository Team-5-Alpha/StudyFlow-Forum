package telerik.project.models.filters;

import lombok.Getter;

import java.util.Optional;

@Getter
public class CommentFilterOptions {

    private Optional<Long> postId;
    private Optional<Long> authorId;
    private Optional<Long> parentCommentId;
    private Optional<String> sortBy;

    public CommentFilterOptions() {
        this(null, null, null, null);
    }

    public CommentFilterOptions(
            Long postId,
            Long authorId,
            Long parentCommentId,
            String sortBy
    ) {
        this.postId = Optional.ofNullable(postId);
        this.authorId = Optional.ofNullable(authorId);
        this.parentCommentId = Optional.ofNullable(parentCommentId);
        this.sortBy = Optional.ofNullable(sortBy);
    }
}
