package telerik.project.security.auth;

import jakarta.servlet.http.HttpServletResponse;
import telerik.project.models.dtos.auth.AuthUserDTO;
import telerik.project.models.dtos.auth.LoginRequestDTO;
import telerik.project.models.dtos.auth.RegisterRequestDTO;

public interface AuthenticationService {

    AuthUserDTO register(RegisterRequestDTO request, HttpServletResponse response);

    AuthUserDTO login(LoginRequestDTO request, HttpServletResponse response);

    void logout(HttpServletResponse response);

    AuthUserDTO getLoggedUser();
}