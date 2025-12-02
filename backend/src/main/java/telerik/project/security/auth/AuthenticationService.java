package telerik.project.security.auth;

import jakarta.servlet.http.HttpServletResponse;
import telerik.project.models.dtos.auth.AuthResponseDTO;
import telerik.project.models.dtos.auth.LoginRequestDTO;
import telerik.project.models.dtos.auth.RegisterRequestDTO;

public interface AuthenticationService {

    AuthResponseDTO register(RegisterRequestDTO request, HttpServletResponse response);

    AuthResponseDTO login(LoginRequestDTO request, HttpServletResponse response);

    AuthResponseDTO logout(HttpServletResponse response);

    AuthResponseDTO getLoggedUser();

}
