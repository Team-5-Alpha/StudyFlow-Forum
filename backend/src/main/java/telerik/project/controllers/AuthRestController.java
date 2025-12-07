package telerik.project.controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import telerik.project.models.dtos.response.ResponseDTO;
import telerik.project.models.dtos.auth.AuthUserDTO;
import telerik.project.models.dtos.auth.LoginRequestDTO;
import telerik.project.models.dtos.auth.RegisterRequestDTO;
import telerik.project.security.auth.AuthenticationService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthenticationService authenticationService;

    @PostMapping("/public/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDTO<AuthUserDTO> register(
            @Valid @RequestBody RegisterRequestDTO request,
            HttpServletResponse response
    ) {
        AuthUserDTO data = authenticationService.register(request, response);
        return ResponseDTO.success("Registration successful.", data);
    }

    @PostMapping("/public/auth/login")
    public ResponseEntity<ResponseDTO<AuthUserDTO>> login(
            @Valid @RequestBody LoginRequestDTO request,
            HttpServletResponse response
    ) {
        var result = authenticationService.login(request, response);
        return ResponseEntity.ok(ResponseDTO.success("Login successful", result));
    }

    @PostMapping("/private/auth/logout")
    public ResponseDTO<?> logout(HttpServletResponse response) {
        authenticationService.logout(response);
        return ResponseDTO.success("Logged out.");
    }

    @GetMapping("/private/auth/me")
    public ResponseDTO<AuthUserDTO> getLoggedUser() {
        AuthUserDTO data = authenticationService.getLoggedUser();
        return ResponseDTO.success("Authenticated.", data);
    }
}