package telerik.project.models;

import lombok.Getter;

@Getter
public enum Role {
    USER("USER"),
    ADMIN("ADMIN");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }
}
