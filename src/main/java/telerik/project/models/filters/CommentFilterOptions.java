package telerik.project.models.filters;

import java.util.Optional;

public class CommentFilterOptions {
    /*postId
    authorId
    parentCommentId
    sortBy*/

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

    public Optional<Long> getPostId() {
        return postId;
    }

    public Optional<Long> getAuthorId() {
        return authorId;
    }

    public Optional<Long> getParentCommentId() {
        return parentCommentId;
    }

    public Optional<String> getSortBy() {
        return sortBy;
    }
}
