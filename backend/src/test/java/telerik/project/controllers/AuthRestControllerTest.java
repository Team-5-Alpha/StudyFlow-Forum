package telerik.project.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import telerik.project.config.SecurityTestConfig;
import telerik.project.config.TestBeans;
import telerik.project.models.dtos.auth.AuthUserDTO;
import telerik.project.models.dtos.auth.LoginRequestDTO;
import telerik.project.models.dtos.auth.RegisterRequestDTO;
import telerik.project.security.auth.AuthenticationService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthRestController.class)
@Import({SecurityTestConfig.class, TestBeans.class})
class AuthRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_ReturnsCreatedResponse() throws Exception {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setUsername("test");
        req.setEmail("test@mail.com");
        req.setFirstName("First");
        req.setLastName("Last");
        req.setPassword("123456");

        AuthUserDTO response = new AuthUserDTO(1L, "test", "USER");

        Mockito.when(authenticationService.register(any(), any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/public/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("test"));

        verify(authenticationService).register(any(), any());
    }

    @Test
    void login_ReturnsOkResponse() throws Exception {
        LoginRequestDTO req = new LoginRequestDTO();
        req.setIdentifier("test");
        req.setPassword("123456");

        AuthUserDTO dto = new AuthUserDTO(1L, "test", "USER");

        Mockito.when(authenticationService.login(any(), any())).thenReturn(dto);

        mockMvc.perform(post("/api/public/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("test"));
    }

    @Test
    void logout_ReturnsSuccess() throws Exception {
        mockMvc.perform(post("/api/private/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(authenticationService).logout(any());
    }

    @Test
    void getLoggedUser_ReturnsUser() throws Exception {
        AuthUserDTO dto = new AuthUserDTO(1L, "test", "USER");

        Mockito.when(authenticationService.getLoggedUser()).thenReturn(dto);

        mockMvc.perform(get("/api/private/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("test"));
    }
}
