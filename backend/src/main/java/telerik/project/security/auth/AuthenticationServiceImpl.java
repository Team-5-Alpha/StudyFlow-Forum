package telerik.project.security.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import telerik.project.exceptions.AuthorizationException;
import telerik.project.exceptions.InvalidOperationException;
import telerik.project.models.User;
import telerik.project.models.dtos.auth.AuthUserDTO;
import telerik.project.models.dtos.auth.LoginRequestDTO;
import telerik.project.models.dtos.auth.RegisterRequestDTO;
import telerik.project.security.jwt.JwtCookieUtil;
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
    public AuthUserDTO register(RegisterRequestDTO request, HttpServletResponse response) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        userService.create(user);

        User created = userService.getByUsername(user.getUsername());

        CustomUserDetails details = new CustomUserDetails(
                created.getId(),
                created.getUsername(),
                created.getEmail(),
                created.getPassword(),
                created.getRole()
        );

        String jwt = jwtService.generateToken(details);
        cookieUtil.addTokenCookie(response, jwt);

        return new AuthUserDTO(
                details.getId(),
                details.getUsername(),
                details.getRole().name()
        );
    }

    @Override
    public AuthUserDTO login(LoginRequestDTO request, HttpServletResponse response) {

        if (request.getIdentifier() == null || request.getIdentifier().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {
            throw new InvalidOperationException("Username/email and password are required.");
        }

        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getIdentifier(),
                            request.getPassword()
                    )
            );

            CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();

            String jwt = jwtService.generateToken(principal);
            cookieUtil.addTokenCookie(response, jwt);

            return new AuthUserDTO(
                    principal.getId(),
                    principal.getUsername(),
                    principal.getRole().name()
            );

        } catch (BadCredentialsException ex) {
            throw new InvalidOperationException("Username/email or password are wrong.");
        }
    }

    @Override
    public void logout(HttpServletResponse response) {
        cookieUtil.clearTokenCookie(response);
        SecurityContextHolder.clearContext();
    }

    @Override
    public AuthUserDTO getLoggedUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == null || !(auth.getPrincipal() instanceof CustomUserDetails principal)) {
            throw new AuthorizationException("Authentication required.");
        }

        return new AuthUserDTO(
                principal.getId(),
                principal.getUsername(),
                principal.getRole().name()
        );
    }
}