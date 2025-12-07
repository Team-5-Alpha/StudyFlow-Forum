package telerik.project.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import telerik.project.models.Role;
import telerik.project.security.auth.CustomUserDetails;

public final class TestSecurityUtils {

    private TestSecurityUtils() {}

    public static void mockAuthenticatedUser() {
        CustomUserDetails principal = new CustomUserDetails(
                1L,
                "test-user",
                "test@mail.com",
                "password",
                Role.USER
        );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()
                )
        );
    }

    public static RequestPostProcessor authenticatedUserRequest() {
        CustomUserDetails principal = new CustomUserDetails(
                1L,
                "test-user",
                "test@mail.com",
                "password",
                Role.USER
        );

        return SecurityMockMvcRequestPostProcessors.authentication(
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()
                )
        );
    }

    public static void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }
}
