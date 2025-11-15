package telerik.project.models.filters;

import lombok.Getter;

import java.util.Optional;

@Getter
public class CommentFilterOptions {

    private final Optional<Long> postId;
    private final Optional<Long> authorId;
    private final Optional<Long> parentCommentId;
    private final Optional<Boolean> isDeleted;
    private final Optional<String> sortBy;
    private final Optional<String> sortOrder;

    public CommentFilterOptions() {
        this.postId = Optional.empty();
        this.authorId = Optional.empty();
        this.parentCommentId = Optional.empty();
        this.isDeleted = Optional.empty();
        this.sortBy = Optional.empty();
        this.sortOrder = Optional.empty();
    }

    public CommentFilterOptions(
            Long postId,
            Long authorId,
            Long parentCommentId,
            Boolean isDeleted,
            String sortBy,
            String sortOrder) {
        this.postId = Optional.ofNullable(postId);
        this.authorId = Optional.ofNullable(authorId);
        this.parentCommentId = Optional.ofNullable(parentCommentId);
        this.isDeleted = Optional.ofNullable(isDeleted);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
    }
}
