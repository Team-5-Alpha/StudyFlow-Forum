package telerik.project.models.filters;

import lombok.Getter;
import telerik.project.utils.PaginationUtils;

import java.util.Optional;

@Getter
public class UserFilterOptions {
    private final Optional<String> username;
    private final Optional<String> email;
    private final Optional<String> firstName;
    private final Optional<Boolean> isBlocked;
    private final Optional<String> sortBy;
    private final Optional<String> sortOrder;

    private final Integer page;
    private final Integer size;

    public UserFilterOptions() {
        this.username = Optional.empty();
        this.email = Optional.empty();
        this.firstName = Optional.empty();
        this.isBlocked = Optional.empty();
        this.sortBy = Optional.empty();
        this.sortOrder = Optional.empty();
        this.page = null;
        this.size = null;
    }

    public UserFilterOptions(String username,
                             String email,
                             String firstName,
                             Boolean isBlocked,
                             String sortBy,
                             String sortOrder,
                             Integer page,
                             Integer size) {
        this.username = Optional.ofNullable(username);
        this.email = Optional.ofNullable(email);
        this.firstName = Optional.ofNullable(firstName);
        this.isBlocked = Optional.ofNullable(isBlocked);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
        this.page = page;
        this.size = size;
    }
}
