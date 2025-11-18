package telerik.project.helpers;

import telerik.project.exceptions.AuthorizationException;
import telerik.project.models.Comment;
import telerik.project.models.Post;
import telerik.project.models.User;

public final class AuthorizationHelper {

    private AuthorizationHelper() {}

    public static boolean isAdmin(User user) {
        return user.isAdmin();
    }

    public static boolean isSelf(User acting, Long targetId) {
        return acting.getId().equals(targetId);
    }

    public static boolean isOwner(User acting, User owner) {
        return acting.getId().equals(owner.getId());
    }

    public static boolean isPostAuthor(User acting, Post post) {
        return acting.getId().equals(post.getAuthor().getId());
    }

    public static boolean isCommentAuthor(User acting, Comment comment) {
        return acting.getId().equals(comment.getAuthor().getId());
    }

    public static boolean isPostAuthorOfComment(User acting, Comment comment) {
        return acting.getId().equals(comment.getPost().getAuthor().getId());
    }

    public static void validateAdmin(User user) {
        if (!isAdmin(user)) {
            throw new AuthorizationException(ExceptionMessages.ADMIN_ONLY);
        }
    }

    public static void validateNotBlocked(User user) {
        if (Boolean.TRUE.equals(user.getIsBlocked())) {
            throw new AuthorizationException(ExceptionMessages.USER_BLOCKED);
        }
    }

    public static void validateSelfOperationNotAllowed(User acting, Long targetId) {
        if (isSelf(acting, targetId)) {
            throw new AuthorizationException(ExceptionMessages.CANNOT_SELF_ACTION);
        }
    }

    public static void validateOwner(User acting, User owner) {
        if (!isOwner(acting, owner)) {
            throw new AuthorizationException(ExceptionMessages.CANNOT_MODIFY_RESOURCE);
        }
    }

    public static void validateCanUpdateResource(boolean allowed) {
        if (!allowed) {
            throw new AuthorizationException(ExceptionMessages.CANNOT_MODIFY_RESOURCE);
        }
    }

    public static void validateCanDeleteResource(boolean allowed) {
        if (!allowed) {
            throw new AuthorizationException(ExceptionMessages.CANNOT_DELETE_RESOURCE);
        }
    }

    @SafeVarargs
    public static void validateAnyOf(BooleanSupplier... conditions) {
        for (BooleanSupplier condition : conditions) {
            if (condition.getAsBoolean()) {
                return;
            }
        }
        throw new AuthorizationException(ExceptionMessages.CANNOT_MODIFY_RESOURCE);
    }

    @FunctionalInterface
    public interface BooleanSupplier {
        boolean getAsBoolean();
    }
}
