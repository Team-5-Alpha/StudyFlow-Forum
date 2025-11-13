package telerik.project.models.filters;

import lombok.Getter;

import java.util.Optional;

@Getter
public class UserFilterOptions {
    private final Optional<String> username;
    private final Optional<String> email;
    private final Optional<String> firstName;
    private final Optional<Boolean> isBlocked;
    private final Optional<String> sortBy;

    public UserFilterOptions() {
        this.username = Optional.empty();
        this.email = Optional.empty();
        this.firstName = Optional.empty();
        this.isBlocked = Optional.empty();
        this.sortBy = Optional.empty();
    }

    public UserFilterOptions(String username,
                             String email,
                             String firstName,
                             Boolean isBlocked,
                             String sortBy) {
        this.username = Optional.ofNullable(username);
        this.email = Optional.ofNullable(email);
        this.firstName = Optional.ofNullable(firstName);
        this.isBlocked = Optional.ofNullable(isBlocked);
        this.sortBy = Optional.ofNullable(sortBy);
    }
}
