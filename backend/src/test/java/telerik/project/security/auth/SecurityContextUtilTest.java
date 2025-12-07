package telerik.project.security.auth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import telerik.project.exceptions.AuthorizationException;
import telerik.project.models.Role;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SecurityContextUtilTest {

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserFields_ReturnsPrincipalData() {
        CustomUserDetails principal = new CustomUserDetails(5L, "user", "mail", "pw", Role.USER);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
        );

        assertThat(SecurityContextUtil.getCurrentUserId()).isEqualTo(5L);
        assertThat(SecurityContextUtil.getCurrentUsername()).isEqualTo("user");
        assertThat(SecurityContextUtil.getCurrentRole()).isEqualTo(Role.USER.name());
    }

    @Test
    void getCurrentUserId_ThrowsWhenAnonymous() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(SecurityContextUtil::getCurrentUserId)
                .isInstanceOf(AuthorizationException.class);
    }
}
