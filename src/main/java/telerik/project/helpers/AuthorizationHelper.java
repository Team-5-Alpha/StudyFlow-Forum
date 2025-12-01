package telerik.project.helpers;

import telerik.project.exceptions.AuthorizationException;
import telerik.project.models.User;
import telerik.project.security.auth.SecurityContextUtil;

public final class AuthorizationHelper {

    private AuthorizationHelper() {}

    private static User actingUser() {
        return SecurityContextUtil.getCurrentUser();
    }

    public static void validateAdmin() {
        User user = actingUser();
        if (!user.isAdmin()) {
            throw new AuthorizationException(String.format(ExceptionMessages.ONLY_ADMIN));
        }
    }

    public static void validateNotBlocked() {
        User user = actingUser();
        if (user.isBlocked()) {
            throw new AuthorizationException(ExceptionMessages.USER_BLOCKED);
        }
    }

    public static void validateSelfOperationNotAllowed(Long targetId) {
        User user = actingUser();
        if (user.getId().equals(targetId)) {
            throw new AuthorizationException(ExceptionMessages.SELF_ACTION);
        }
    }

    public static void validateOwner(User owner) {
        User user = actingUser();
        if (!user.getId().equals(owner.getId())) {
            throw new AuthorizationException(
                    String.format(ExceptionMessages.NOT_OWNER, "resource", owner.getUsername())
            );
        }
    }

    public static void validateOwnerOrAdmin(User owner) {
        User user = actingUser();
        if (!user.getId().equals(owner.getId()) && !user.isAdmin()) {
            throw new AuthorizationException(
                    String.format(ExceptionMessages.NOT_OWNER, "resource", owner.getUsername())
            );
        }
    }
}
