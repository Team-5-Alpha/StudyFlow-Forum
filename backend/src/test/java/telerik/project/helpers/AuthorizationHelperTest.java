package telerik.project.helpers;

import org.junit.jupiter.api.Test;
import telerik.project.exceptions.AuthorizationException;
import telerik.project.models.Role;
import telerik.project.models.User;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthorizationHelperTest {

    @Test
    void validateAdmin_ThrowsForNonAdmin() {
        User user = new User();
        user.setRole(Role.USER);

        assertThatThrownBy(() -> AuthorizationHelper.validateAdmin(user))
                .isInstanceOf(AuthorizationException.class);
    }

    @Test
    void validateNotBlocked_ThrowsWhenBlocked() {
        User user = new User();
        user.setIsBlocked(true);

        assertThatThrownBy(() -> AuthorizationHelper.validateNotBlocked(user))
                .isInstanceOf(AuthorizationException.class);
    }

    @Test
    void validateOwnerOrAdmin_AllowsAdminWhenNotOwner() {
        User admin = new User();
        admin.setId(1L);
        admin.setRole(Role.ADMIN);

        User owner = new User();
        owner.setId(2L);

        AuthorizationHelper.validateOwnerOrAdmin(admin, owner);
    }

    @Test
    void validateOwnerOrAdmin_ThrowsForNonOwnerNonAdmin() {
        User acting = new User();
        acting.setId(1L);
        acting.setRole(Role.USER);

        User owner = new User();
        owner.setId(2L);

        assertThatThrownBy(() -> AuthorizationHelper.validateOwnerOrAdmin(acting, owner))
                .isInstanceOf(AuthorizationException.class);
    }
}
