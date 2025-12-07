package telerik.project.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import telerik.project.config.SecurityTestConfig;
import telerik.project.config.TestBeans;
import telerik.project.config.TestSecurityUtils;
import telerik.project.helpers.mappers.PostMapper;
import telerik.project.helpers.mappers.UserMapper;
import telerik.project.models.User;
import telerik.project.models.dtos.response.UserResponseDTO;
import telerik.project.services.contracts.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserRestController.class)
@Import({SecurityTestConfig.class, TestBeans.class})
class UserRestControllerTest {

    @Autowired MockMvc mockMvc;

    @Autowired UserService userService;
    @Autowired UserMapper userMapper;
    @Autowired PostMapper postMapper;

    @Autowired ObjectMapper objectMapper;

    @BeforeEach
    void setUpSecurityContext() {
        TestSecurityUtils.mockAuthenticatedUser();
    }

    @AfterEach
    void clearSecurityContext() {
        TestSecurityUtils.clearAuthentication();
    }

    @Test
    void getById_ReturnsUserResponse() throws Exception {
        User user = new User();
        user.setId(1L);

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(1L);

        Mockito.when(userService.getById(1L)).thenReturn(user);
        Mockito.when(userMapper.toResponse(any(), any())).thenReturn(dto);

        mockMvc.perform(get("/api/private/users/1")
                        .with(TestSecurityUtils.authenticatedUserRequest()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void deleteUser_CallsService() throws Exception {
        mockMvc.perform(delete("/api/private/users/1")
                        .with(TestSecurityUtils.authenticatedUserRequest()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(userService).delete(1L);
    }

    @Test
    void followUser_Works() throws Exception {
        mockMvc.perform(post("/api/private/users/5/follow")
                        .with(TestSecurityUtils.authenticatedUserRequest()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(userService).followUser(5L);
    }
}
