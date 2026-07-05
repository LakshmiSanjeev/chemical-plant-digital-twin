package cpdt.backend.controllers;

import cpdt.backend.entities.TelemetryEntity;
import cpdt.backend.services.TelemetryQueryService;
import cpdt.common.enums.ProcessArea;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TelemetryController.class)
class TelemetryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TelemetryQueryService telemetryQueryService;

    @Test
    void shouldReturnDeviceTelemetryHistory() throws Exception {
        TelemetryEntity telemetry = new TelemetryEntity();
        telemetry.setDeviceId("TEMP-001");

        when(telemetryQueryService.getDeviceHistory("TEMP-001", 50)).thenReturn(List.of(telemetry));

        mockMvc.perform(get("/api/telemetry/TEMP-001")
                        .param("limit", "50"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].deviceId").value("TEMP-001"));

        verify(telemetryQueryService).getDeviceHistory("TEMP-001", 50);
    }

    @Test
    void shouldUseDefaultLimitForDeviceHistory() throws Exception {
        when(telemetryQueryService.getDeviceHistory("TEMP-001", 100)).thenReturn(List.of());
        mockMvc.perform(get("/api/telemetry/TEMP-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        verify(telemetryQueryService).getDeviceHistory("TEMP-001", 100);
    }

    @Test
    void shouldReturnAreaTelemetryHistory() throws Exception {
        TelemetryEntity telemetry = new TelemetryEntity();
        telemetry.setDeviceId("PRESS-001");

        when(telemetryQueryService.getAreaHistory(ProcessArea.REACTOR_SECTION, 1000L, 25)).thenReturn(List.of(telemetry));

        mockMvc.perform(get("/api/telemetry/area/REACTOR_SECTION")
                        .param("since", "1000")
                        .param("limit", "25"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].deviceId").value("PRESS-001"));

        verify(telemetryQueryService).getAreaHistory(ProcessArea.REACTOR_SECTION, 1000L, 25);
    }

    @Test
    void shouldUseDefaultLimitForAreaHistory() throws Exception {
        when(telemetryQueryService.getAreaHistory(ProcessArea.REACTOR_SECTION, 5000L, 100)).thenReturn(List.of());

        mockMvc.perform(get("/api/telemetry/area/REACTOR_SECTION")
                        .param("since", "5000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(telemetryQueryService).getAreaHistory(ProcessArea.REACTOR_SECTION, 5000L, 100);
    }

    @Test
    void shouldReturnBadRequestForInvalidProcessArea() throws Exception {
        mockMvc.perform(get("/api/telemetry/area/INVALID").param("since", "1000"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenSinceParameterIsMissing() throws Exception {
        mockMvc.perform(get("/api/telemetry/area/REACTOR_SECTION")).andExpect(status().isBadRequest());
    }
}