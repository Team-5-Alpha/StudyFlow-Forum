package telerik.project.models.filters;

import lombok.Getter;

import java.util.Optional;

@Getter
public class PostFilterOptions {
    private final Optional<String> title;
    private final Optional<String> author;
    private final Optional<String> tag;
    private final Optional<Boolean> isDeleted;
    private final Optional<String> sortBy;

    public PostFilterOptions() {
        this.title = Optional.empty();
        this.author = Optional.empty();
        this.tag = Optional.empty();
        this.isDeleted = Optional.empty();
        this.sortBy = Optional.empty();
    }

    public PostFilterOptions(String title,
                             String author,
                             String tag,
                             Boolean isDeleted,
                             String sortBy) {
        this.title = Optional.ofNullable(title);
        this.author = Optional.ofNullable(author);
        this.tag = Optional.ofNullable(tag);
        this.isDeleted = Optional.ofNullable(isDeleted);
        this.sortBy = Optional.ofNullable(sortBy);
    }
}
