package telerik.project.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    USER("User"),
    ADMIN("Administrator");

    private final String displayName;
}
