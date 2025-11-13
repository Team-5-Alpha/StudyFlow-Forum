package telerik.project.models.filters;

import lombok.Getter;

import java.util.Optional;

@Getter
public class PostFilterOptions {
    private final Optional<String> title;
    private final Optional<String> contentKeyword;
    private final Optional<Long> authorId;
    private final Optional<String> tag;
    private final Optional<Boolean> isDeleted;
    private final Optional<String> sortBy;
    private final Optional<String> sortOrder;

    public PostFilterOptions() {
        this.title = Optional.empty();
        this.contentKeyword = Optional.empty();
        this.authorId = Optional.empty();
        this.tag = Optional.empty();
        this.isDeleted = Optional.empty();
        this.sortBy = Optional.empty();
        this.sortOrder = Optional.empty();
    }

    public PostFilterOptions(String title,
                             String contentKeyword,
                             Long authorId,
                             String tag,
                             Boolean isDeleted,
                             String sortBy,
                             String sortOrder) {
        this.title = Optional.ofNullable(title);
        this.contentKeyword = Optional.ofNullable(contentKeyword);
        this.authorId = Optional.ofNullable(authorId);
        this.tag = Optional.ofNullable(tag);
        this.isDeleted = Optional.ofNullable(isDeleted);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
    }
}
