package telerik.project.security.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import telerik.project.models.User;
import telerik.project.security.jwt.JwtCookieUtil;
import telerik.project.models.dtos.auth.AuthResponseDTO;
import telerik.project.models.dtos.auth.LoginRequestDTO;
import telerik.project.models.dtos.auth.RegisterRequestDTO;
import telerik.project.security.jwt.JwtService;
import telerik.project.services.contracts.UserService;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtCookieUtil cookieUtil;
    private final UserService userService;

    @Override
    public AuthResponseDTO register(RegisterRequestDTO request, HttpServletResponse response) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        userService.create(user);

        String jwt = jwtService.generateToken(new CustomUserDetails(user));
        cookieUtil.addTokenCookie(response, jwt);

        return new AuthResponseDTO("Registration successful.", user.getUsername(), user.getRole().name());
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO request, HttpServletResponse response) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getIdentifier(),
                        request.getPassword()
                )
        );

        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();

        String jwt = jwtService.generateToken(principal);
        cookieUtil.addTokenCookie(response, jwt);

        return new AuthResponseDTO(
                "Login successful", principal.getUsername(), principal.getUser().getRole().name()
        );
    }

    @Override
    public AuthResponseDTO logout(HttpServletResponse response) {
        cookieUtil.clearTokenCookie(response);
        SecurityContextHolder.clearContext();
        return new AuthResponseDTO("Logged out", null, "Anonymous");
    }

    @Override
    public AuthResponseDTO getLoggedUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return new AuthResponseDTO("Not authenticated", null, "Anonymous");
        }

        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();

        return new AuthResponseDTO(
                "Authenticated", principal.getUsername(), principal.getUser().getRole().name()
        );
    }
}
