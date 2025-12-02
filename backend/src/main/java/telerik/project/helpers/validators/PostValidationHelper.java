package telerik.project.helpers.validators;

import telerik.project.exceptions.InvalidOperationException;
import telerik.project.helpers.ExceptionMessages;
import telerik.project.models.Post;

public final class PostValidationHelper {

    private PostValidationHelper() {}

    public static void validateNotDeleted(Post post) {
        if (post.isDeleted()) {
            throw new InvalidOperationException(String.format(ExceptionMessages.ENTITY_DELETED, "post"));
        }
    }
}
