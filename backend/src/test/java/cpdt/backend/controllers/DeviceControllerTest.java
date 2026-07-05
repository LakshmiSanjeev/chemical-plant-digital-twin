package cpdt.backend.controllers;

import cpdt.backend.entities.DeviceEntity;
import cpdt.backend.repositories.DeviceRepository;

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

@WebMvcTest(DeviceController.class)
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeviceRepository deviceRepository;

    @Test
    void shouldReturnAllDevices() throws Exception {
        DeviceEntity device1 = new DeviceEntity();
        device1.setDeviceId("TEMP-001");

        DeviceEntity device2 = new DeviceEntity();
        device2.setDeviceId("PRESS-001");

        when(deviceRepository.findAll()).thenReturn(List.of(device1, device2));

        mockMvc.perform(get("/api/devices"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].deviceId").value("TEMP-001"))
                .andExpect(jsonPath("$[1].deviceId").value("PRESS-001"));
        verify(deviceRepository).findAll();
    }

    @Test
    void shouldReturnDeviceWhenPresent() throws Exception {
        DeviceEntity device = new DeviceEntity();
        device.setDeviceId("TEMP-001");

        when(deviceRepository.findById("TEMP-001")).thenReturn(Optional.of(device));

        mockMvc.perform(get("/api/devices/TEMP-001"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deviceId").value("TEMP-001"));

        verify(deviceRepository).findById("TEMP-001");
    }

    @Test
    void shouldReturn404WhenDeviceNotFound() throws Exception {
        when(deviceRepository.findById("UNKNOWN")).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/devices/UNKNOWN")).andExpect(status().isNotFound());
        verify(deviceRepository).findById("UNKNOWN");
    }

    @Test
    void shouldReturnEmptyListWhenNoDevicesExist() throws Exception {
        when(deviceRepository.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        verify(deviceRepository).findAll();
    }
}