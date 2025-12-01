package telerik.project.security.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import telerik.project.exceptions.AuthorizationException;
import telerik.project.models.User;

public class SecurityContextUtil {

    public static User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal().equals("anonymousUser")) {
            throw new AuthorizationException("Authentication required.");
        }

        return ((CustomUserDetails) auth.getPrincipal()).getUser();
    }
}
