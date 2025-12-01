package telerik.project.controllers.rest;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import telerik.project.security.auth.AuthenticationService;
import telerik.project.models.dtos.auth.AuthResponseDTO;
import telerik.project.models.dtos.auth.LoginRequestDTO;
import telerik.project.models.dtos.auth.RegisterRequestDTO;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthenticationService authenticationService;

    @PreAuthorize("permitAll()")
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO request,
            HttpServletResponse response
    ) {
        AuthResponseDTO result = authenticationService.register(request, response);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request,
            HttpServletResponse response
    ) {
        AuthResponseDTO result = authenticationService.login(request, response);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/logout")
    public ResponseEntity<AuthResponseDTO> logout(HttpServletResponse response) {
        AuthResponseDTO result = authenticationService.logout(response);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<AuthResponseDTO> getLogoutUser() {
        AuthResponseDTO result = authenticationService.getLoggedUser();
        return ResponseEntity.ok(result);
    }
}
