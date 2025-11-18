package telerik.project.helpers;

import telerik.project.exceptions.AuthorizationException;
import telerik.project.models.User;

public final class AuthorizationHelper {

    private AuthorizationHelper() {}

    public static void validateAdmin(User user) {
        if (!user.isAdmin()) {
            throw new AuthorizationException(ExceptionMessages.ADMIN_ONLY);
        }
    }

    public static void validateNotBlocked(User user) {
        if (Boolean.TRUE.equals(user.getIsBlocked())) {
            throw new AuthorizationException(ExceptionMessages.USER_BLOCKED);
        }
    }

    public static void validateSelfOperationNotAllowed(User acting, Long targetId) {
        if (acting.getId().equals(targetId)) {
            throw new AuthorizationException(ExceptionMessages.CANNOT_SELF_ACTION);
        }
    }

    public static void validateOwner(User acting, User owner) {
        if (!acting.getId().equals(owner.getId())) {
            throw new AuthorizationException(ExceptionMessages.CANNOT_MODIFY_RESOURCE);
        }
    }

    public static void validateOwnerOrAdmin(User acting, User owner) {
        if (!acting.getId().equals(owner.getId()) || !acting.isAdmin()) {
            throw new AuthorizationException(ExceptionMessages.CANNOT_MODIFY_RESOURCE);
        }
    }
}
