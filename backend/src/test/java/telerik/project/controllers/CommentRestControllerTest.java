package telerik.project.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import telerik.project.config.SecurityTestConfig;
import telerik.project.config.TestSecurityUtils;
import telerik.project.helpers.mappers.CommentMapper;
import telerik.project.models.Comment;
import telerik.project.models.dtos.response.CommentResponseDTO;
import telerik.project.services.contracts.CommentService;
import telerik.project.config.TestBeans;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentRestController.class)
@Import({SecurityTestConfig.class, TestBeans.class})
class CommentRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    CommentService commentService;

    @Autowired
    CommentMapper commentMapper;

    @BeforeEach
    void setUpSecurityContext() {
        TestSecurityUtils.mockAuthenticatedUser();
    }

    @AfterEach
    void clearSecurityContext() {
        TestSecurityUtils.clearAuthentication();
    }

    @Test
    void getById_ReturnsComment() throws Exception {
        Comment c = new Comment();
        c.setId(55L);

        CommentResponseDTO dto = new CommentResponseDTO();
        dto.setId(55L);

        Mockito.when(commentService.getById(55L)).thenReturn(c);
        Mockito.when(commentMapper.toResponse(any(), any())).thenReturn(dto);

        mockMvc.perform(get("/api/private/comments/55")
                        .with(TestSecurityUtils.authenticatedUserRequest()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(55));
    }

    @Test
    void deleteComment_CallsService() throws Exception {
        mockMvc.perform(delete("/api/private/comments/10")
                        .with(TestSecurityUtils.authenticatedUserRequest()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(commentService).delete(10L);
    }
}
