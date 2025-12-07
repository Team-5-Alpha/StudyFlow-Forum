package telerik.project.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import telerik.project.config.SecurityTestConfig;
import telerik.project.helpers.mappers.NotificationMapper;
import telerik.project.models.Notification;
import telerik.project.models.dtos.response.NotificationResponseDTO;
import telerik.project.services.contracts.NotificationService;
import telerik.project.config.TestBeans;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationRestController.class)
@Import({SecurityTestConfig.class, TestBeans.class})
class NotificationRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    NotificationService notificationService;

    @Autowired
    NotificationMapper notificationMapper;

    @Test
    void getById_ReturnsNotification() throws Exception {
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(10L);

        Mockito.when(notificationService.getById(10L)).thenReturn(new Notification());
        Mockito.when(notificationMapper.toResponse(any())).thenReturn(dto);

        mockMvc.perform(get("/api/private/notifications/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(10));
    }

    @Test
    void deleteNotification_CallsService() throws Exception {
        mockMvc.perform(delete("/api/private/notifications/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(notificationService).delete(10L);
    }
}