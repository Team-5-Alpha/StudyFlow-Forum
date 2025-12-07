package telerik.project.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import telerik.project.config.SecurityTestConfig;
import telerik.project.config.TestBeans;
import telerik.project.config.TestSecurityUtils;
import telerik.project.helpers.mappers.CommentMapper;
import telerik.project.helpers.mappers.PostMapper;
import telerik.project.models.Post;
import telerik.project.models.dtos.create.PostCreateDTO;
import telerik.project.models.dtos.response.PostResponseDTO;
import telerik.project.services.contracts.CommentService;
import telerik.project.services.contracts.PostService;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostRestController.class)
@Import({SecurityTestConfig.class, TestBeans.class})
class PostRestControllerTest {

    @Autowired MockMvc mockMvc;

    @Autowired PostService postService;
    @Autowired CommentService commentService;
    @Autowired PostMapper postMapper;
    @Autowired CommentMapper commentMapper;

    @BeforeEach
    void setUpSecurityContext() {
        TestSecurityUtils.mockAuthenticatedUser();
    }

    @AfterEach
    void clearSecurityContext() {
        TestSecurityUtils.clearAuthentication();
    }

    @Test
    void getById_ReturnsPost() throws Exception {
        Post p = new Post();
        p.setId(10L);

        PostResponseDTO resp = new PostResponseDTO();
        resp.setId(10L);

        Mockito.when(postService.getById(10L)).thenReturn(p);
        Mockito.when(postMapper.toResponse(any(), any())).thenReturn(resp);

        mockMvc.perform(get("/api/private/posts/10")
                        .with(TestSecurityUtils.authenticatedUserRequest()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(10));
    }

    @Test
    void createPost_ReturnsSuccess() throws Exception {
        PostCreateDTO dto = new PostCreateDTO();
        dto.setTitle("valid post title");
        dto.setContent("This is a valid content body that exceeds thirty-two characters.");

        Post created = new Post();
        created.setId(99L);

        Mockito.when(postService.create(any())).thenReturn(created);
        Mockito.when(postMapper.toResponse(any(), any())).thenReturn(new PostResponseDTO());

        mockMvc.perform(post("/api/private/posts")
                        .with(TestSecurityUtils.authenticatedUserRequest())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
