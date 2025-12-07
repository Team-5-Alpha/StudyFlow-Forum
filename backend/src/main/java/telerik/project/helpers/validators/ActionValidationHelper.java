package telerik.project.helpers.validators;

import telerik.project.exceptions.InvalidOperationException;
import telerik.project.helpers.ExceptionMessages;
import telerik.project.models.Comment;
import telerik.project.models.Post;
import telerik.project.models.User;

public final class ActionValidationHelper {

    private ActionValidationHelper() {}

    public static void validateCanFollow(User actingUser, User target) {
        if (actingUser.follows(target)) {
            throw new InvalidOperationException(
                    String.format(ExceptionMessages.ALREADY_FOLLOWING, target.getUsername())
            );
        }
    }

    public static void validateCanUnfollow(User actingUser, User target) {
        if (!actingUser.follows(target)) {
            throw new InvalidOperationException(
                    String.format(ExceptionMessages.NOT_FOLLOWING, target.getUsername())
            );
        }
    }

    public static void validateCanBlock(User target) {
        if (target.isBlocked()) {
            throw new InvalidOperationException(
                    String.format(ExceptionMessages.USER_ALREADY_BLOCKED, target.getUsername())
            );
        }
    }

    public static void validateCanUnblock(User target) {
        if (!target.isBlocked()) {
            throw new InvalidOperationException(
                    String.format(ExceptionMessages.USER_NOT_BLOCKED, target.getUsername())
            );
        }
    }

    public static void validateCanPromote(User target) {
        if (target.isAdmin()) {
            throw new InvalidOperationException(
                    String.format(ExceptionMessages.USER_ALREADY_ADMIN, target.getUsername())
            );
        }
    }

    public static void validateCanLike(User actingUser, Post post) {
        if (actingUser.hasLiked(post)) {
            throw new InvalidOperationException(
                    String.format(ExceptionMessages.ALREADY_LIKED, "post")
            );
        }
    }

    public static void validateCanUnlike(User actingUser, Post post) {
        if (!actingUser.hasLiked(post)) {
            throw new InvalidOperationException(
                    String.format(ExceptionMessages.NOT_LIKED, "post")
            );
        }
    }

    public static void validateCanLike(User actingUser, Comment comment) {
        if (actingUser.hasLiked(comment)) {
            throw new InvalidOperationException(
                    String.format(ExceptionMessages.ALREADY_LIKED, "comment")
            );
        }
    }

    public static void validateCanUnlike(User actingUser, Comment comment) {
        if (!actingUser.hasLiked(comment)) {
            throw new InvalidOperationException(
                    String.format(ExceptionMessages.NOT_LIKED, "comment")
            );
        }
    }
}