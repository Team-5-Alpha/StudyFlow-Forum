package telerik.project.helpers.validators;

import telerik.project.exceptions.AuthorizationException;
import telerik.project.helpers.ExceptionMessages;
import telerik.project.models.Post;

public final class PostValidationHelper {

    private PostValidationHelper() {}

    public static void validateNotDeleted(Post post) {
        if (Boolean.TRUE.equals(post.getIsDeleted())) {
            throw new AuthorizationException(ExceptionMessages.POST_DELETED);
        }
    }
}
