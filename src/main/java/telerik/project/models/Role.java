package telerik.project.models;

import lombok.Getter;

@Getter
public enum Role {
    USER("User"),
    ADMIN("Administrator");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }
}
