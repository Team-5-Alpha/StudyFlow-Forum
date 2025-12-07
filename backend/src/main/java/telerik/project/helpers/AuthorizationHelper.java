package telerik.project.helpers;

import telerik.project.exceptions.AuthorizationException;
import telerik.project.models.User;

public final class AuthorizationHelper {

    private AuthorizationHelper() {}

    public static void validateAdmin(User actingUser) {
        if (!actingUser.isAdmin()) {
            throw new AuthorizationException(ExceptionMessages.ONLY_ADMIN);
        }
    }

    public static void validateNotBlocked(User actingUser) {
        if (actingUser.isBlocked()) {
            throw new AuthorizationException(ExceptionMessages.USER_BLOCKED);
        }
    }

    public static void validateSelfOperationNotAllowed(User actingUser, Long targetId) {
        if (actingUser.getId().equals(targetId)) {
            throw new AuthorizationException(ExceptionMessages.SELF_ACTION);
        }
    }

    public static void validateOwner(User actingUser, User owner) {
        if (!actingUser.getId().equals(owner.getId())) {
            throw new AuthorizationException(
                    String.format(ExceptionMessages.NOT_OWNER, "resource", owner.getUsername())
            );
        }
    }

    public static void validateOwnerOrAdmin(User actingUser, User owner) {
        boolean isOwner = actingUser.getId().equals(owner.getId());
        if (!isOwner && !actingUser.isAdmin()) {
            throw new AuthorizationException(
                    String.format(ExceptionMessages.NOT_OWNER, "resource", owner.getUsername())
            );
        }
    }
}