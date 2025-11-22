package telerik.project.models.filters;

import lombok.Getter;
import telerik.project.utils.PaginationUtils;

import java.util.Optional;

@Getter
public class UserFilterOptions {
    private final Optional<String> username;
    private final Optional<String> firstName;
    private final Optional<String> lastName;
    private final Optional<String> email;
    private final Optional<Boolean> isBlocked;
    private final Optional<String> sortBy;
    private final Optional<String> sortOrder;

    private final Integer page;
    private final Integer size;

    public UserFilterOptions() {
        this.username = Optional.empty();
        this.firstName = Optional.empty();
        this.lastName = Optional.empty();
        this.email = Optional.empty();
        this.isBlocked = Optional.empty();
        this.sortBy = Optional.empty();
        this.sortOrder = Optional.empty();
        this.page = null;
        this.size = null;
    }

    public UserFilterOptions(String username,
                             String firstName,
                             String lastName,
                             String email,
                             Boolean isBlocked,
                             String sortBy,
                             String sortOrder,
                             Integer page,
                             Integer size) {
        this.username = Optional.ofNullable(username);
        this.firstName = Optional.ofNullable(firstName);
        this.lastName = Optional.ofNullable(lastName);
        this.email = Optional.ofNullable(email);
        this.isBlocked = Optional.ofNullable(isBlocked);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
        this.page = page;
        this.size = size;
    }
}
