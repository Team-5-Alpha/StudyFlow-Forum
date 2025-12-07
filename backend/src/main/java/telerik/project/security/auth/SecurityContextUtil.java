package telerik.project.security.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import telerik.project.exceptions.AuthorizationException;

public class SecurityContextUtil {

    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == null || auth.getPrincipal().equals("anonymousUser")) {
            throw new AuthorizationException("Authentication required.");
        }

        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        return principal.getId();
    }

    public static String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == null || auth.getPrincipal().equals("anonymousUser")) {
            throw new AuthorizationException("Authentication required.");
        }

        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        return principal.getUsername();
    }

    public static String getCurrentRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == null || auth.getPrincipal().equals("anonymousUser")) {
            throw new AuthorizationException("Authentication required.");
        }

        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        return principal.getRole().name();
    }
}