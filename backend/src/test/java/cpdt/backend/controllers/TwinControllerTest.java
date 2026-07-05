package cpdt.backend.controllers;

import cpdt.backend.twin.TwinDeviceState;
import cpdt.backend.twin.TwinStateStore;
import cpdt.common.enums.ProcessArea;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TwinController.class)
class TwinControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TwinStateStore twinStateStore;

    @Test
    void shouldReturnAllTwinStates() throws Exception {
        TwinDeviceState state1 = new TwinDeviceState("TEMP-001");
        TwinDeviceState state2 = new TwinDeviceState("TEMP-001");

        when(twinStateStore.getAllStates()).thenReturn(List.of(state1, state2));
        mockMvc.perform(get("/api/twin"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2));
        verify(twinStateStore).getAllStates();
    }

    @Test
    void shouldReturnEmptyTwinStateCollection() throws Exception {
        when(twinStateStore.getAllStates()).thenReturn(List.of());
        mockMvc.perform(get("/api/twin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        verify(twinStateStore).getAllStates();
    }

    @Test
    void shouldReturnDeviceStateWhenPresent() throws Exception {
        TwinDeviceState state = new TwinDeviceState("TEMP-001");
        when(twinStateStore.getState("TEMP-001")).thenReturn(Optional.of(state));
        mockMvc.perform(get("/api/twin/TEMP-001")).andExpect(status().isOk());
        verify(twinStateStore).getState("TEMP-001");
    }

    @Test
    void shouldReturn404WhenDeviceStateNotFound() throws Exception {
        when(twinStateStore.getState("UNKNOWN")).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/twin/UNKNOWN")).andExpect(status().isNotFound());
        verify(twinStateStore).getState("UNKNOWN");
    }

    @Test
    void shouldReturnAreaStates() throws Exception {
        TwinDeviceState state = new TwinDeviceState("TEMP-001");
        when(twinStateStore.getStatesByProcessArea(ProcessArea.REACTOR_SECTION)).thenReturn(List.of(state));
        mockMvc.perform(get("/api/twin/area/REACTOR_SECTION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        verify(twinStateStore).getStatesByProcessArea(ProcessArea.REACTOR_SECTION);
    }

    @Test
    void shouldReturnBadRequestForInvalidProcessArea() throws Exception {
        mockMvc.perform(get("/api/twin/area/INVALID")).andExpect(status().isBadRequest());
    }
}