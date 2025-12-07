package telerik.project.helpers.validators;

import telerik.project.exceptions.InvalidOperationException;
import telerik.project.helpers.ExceptionMessages;
import telerik.project.models.Comment;

public final class CommentValidationHelper {

    private CommentValidationHelper() {
    }

    public static void validateNotDeleted(Comment comment) {
        if (comment.isDeleted()) {
            throw new InvalidOperationException(String.format(ExceptionMessages.ENTITY_DELETED, "comment"));
        }
    }

    public static void validateReplySamePost(Comment parent, Long postId) {
        if (!parent.getPost().getId().equals(postId)) {
            throw new InvalidOperationException(
                    String.format(ExceptionMessages.WRONG_POST_REPLY, postId)
            );
        }
    }

    public static void validateParentNotDeleted(Comment parent) {
        if (parent.isDeleted()) {
            throw new InvalidOperationException(String.format(ExceptionMessages.REPLY_TO_DELETED));
        }
    }
}
