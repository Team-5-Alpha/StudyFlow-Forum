package telerik.project.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import telerik.project.config.SecurityTestConfig;
import telerik.project.helpers.mappers.TagMapper;
import telerik.project.models.Tag;
import telerik.project.models.dtos.response.TagResponseDTO;
import telerik.project.services.contracts.TagService;
import telerik.project.config.TestBeans;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TagRestController.class)
@Import({SecurityTestConfig.class, TestBeans.class})
class TagRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TagService tagService;

    @Autowired
    TagMapper tagMapper;

    @Test
    void getTagById_ReturnsTag() throws Exception {
        Tag tag = new Tag();
        tag.setId(1L);

        TagResponseDTO dto = new TagResponseDTO();
        dto.setId(1L);

        when(tagService.getById(1L)).thenReturn(tag);
        when(tagMapper.toResponse(tag)).thenReturn(dto);

        mockMvc.perform(get("/api/private/tags/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }
}