package telerik.project.helpers.validators;

import telerik.project.exceptions.AuthorizationException;
import telerik.project.helpers.ExceptionMessages;
import telerik.project.models.Comment;

public final class CommentValidationHelper {

    private CommentValidationHelper() {}

    public static void validateNotDeleted(Comment comment) {
        if (Boolean.TRUE.equals(comment.getIsDeleted())) {
            throw new AuthorizationException(ExceptionMessages.COMMENT_DELETED);
        }
    }

    public static void validateReplySamePost(Comment parent, Long postId) {
        if (!parent.getPost().getId().equals(postId)) {
            throw new AuthorizationException(ExceptionMessages.REPLY_WRONG_POST);
        }
    }

    public static void validateParentNotDeleted(Comment parent) {
        if (Boolean.TRUE.equals(parent.getIsDeleted())) {
            throw new AuthorizationException(ExceptionMessages.REPLY_TO_DELETED);
        }
    }
}
