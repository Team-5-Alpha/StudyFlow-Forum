package telerik.project.helpers;

import org.junit.jupiter.api.Test;
import telerik.project.exceptions.InvalidOperationException;
import telerik.project.helpers.validators.ActionValidationHelper;
import telerik.project.models.Comment;
import telerik.project.models.Post;
import telerik.project.models.User;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ActionValidationHelperTest {

    @Test
    void validateCanFollow_ThrowsWhenAlreadyFollowing() {
        User acting = new User();
        User target = new User();
        acting.getFollowing().add(target);

        assertThatThrownBy(() -> ActionValidationHelper.validateCanFollow(acting, target))
                .isInstanceOf(InvalidOperationException.class);
    }

    @Test
    void validateCanLikePost_ThrowsWhenAlreadyLiked() {
        User acting = new User();
        Post post = new Post();
        acting.getLikedPosts().add(post);

        assertThatThrownBy(() -> ActionValidationHelper.validateCanLike(acting, post))
                .isInstanceOf(InvalidOperationException.class);
    }

    @Test
    void validateCanUnlikeComment_ThrowsWhenNotLiked() {
        User acting = new User();
        Comment comment = new Comment();

        assertThatThrownBy(() -> ActionValidationHelper.validateCanUnlike(acting, comment))
                .isInstanceOf(InvalidOperationException.class);
    }
}
