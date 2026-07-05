package cpdt.backend.controllers;

import cpdt.backend.entities.AlertEntity;
import cpdt.backend.services.AlertFetch;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AlertController.class)
class AlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AlertFetch alertFetch;

    @Test
    void shouldReturnAllAlerts() throws Exception {
        AlertEntity alert = new AlertEntity();
        Page<AlertEntity> page = new PageImpl<>(List.of(alert), PageRequest.of(0, 20), 1);
        when(alertFetch.getAllAlerts(any())).thenReturn(page);
        mockMvc.perform(get("/api/alerts")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
        verify(alertFetch).getAllAlerts(any());
    }

    @Test
    void shouldReturnEmptyPageWhenNoAlertsExist() throws Exception {
        Page<AlertEntity> page = Page.empty();
        when(alertFetch.getAllAlerts(any())).thenReturn(page);
        mockMvc.perform(get("/api/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
        verify(alertFetch).getAllAlerts(any());
    }

    @Test
    void shouldReturnActiveAlerts() throws Exception {
        AlertEntity alert = new AlertEntity();
        when(alertFetch.getActiveAlerts()).thenReturn(List.of(alert));
        mockMvc.perform(get("/api/alerts/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        verify(alertFetch).getActiveAlerts();
    }

    @Test
    void shouldReturnEmptyActiveAlerts() throws Exception {
        when(alertFetch.getActiveAlerts()).thenReturn(List.of());
        mockMvc.perform(get("/api/alerts/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        verify(alertFetch).getActiveAlerts();
    }

    @Test
    void shouldAcknowledgeAlert() throws Exception {
        doNothing().when(alertFetch).acknowledge("ALERT-001");
        mockMvc.perform(post("/api/alerts/ALERT-001/acknowledge")).andExpect(status().isNoContent());
        verify(alertFetch).acknowledge("ALERT-001");
    }
}