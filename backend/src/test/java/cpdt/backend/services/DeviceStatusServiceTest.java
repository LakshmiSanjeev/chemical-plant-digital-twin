package cpdt.backend.services;

import cpdt.backend.entities.DeviceEntity;
import cpdt.backend.mqtt.DeviceStatusPublisher;
import cpdt.backend.repositories.DeviceRepository;
import cpdt.common.dto.DeviceStatusMessage;
import cpdt.common.enums.DeviceStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceStatusServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DeviceStatusPublisher deviceStatusPublisher;

    @InjectMocks
    private DeviceStatusService deviceStatusService;

    @Test
    void shouldThrowWhenDeviceNotFound() {
        when(deviceRepository.findById("DEVICE-001")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> deviceStatusService.updateStatus("DEVICE-001", DeviceStatus.WARNING, "Reason"));
        verify(deviceRepository).findById("DEVICE-001");
        verifyNoMoreInteractions(deviceRepository);
        verifyNoInteractions(deviceStatusPublisher);
    }

    @Test
    void shouldNotUpdateWhenStatusIsUnchanged() {
        DeviceEntity device = new DeviceEntity();
        device.setStatus(DeviceStatus.ONLINE);
        when(deviceRepository.findById("DEVICE-001")).thenReturn(Optional.of(device));
        deviceStatusService.updateStatus("DEVICE-001", DeviceStatus.ONLINE, "Already online");
        verify(deviceRepository).findById("DEVICE-001");
        verify(deviceRepository, never()).save(any());
        verifyNoInteractions(deviceStatusPublisher);
    }

    @Test
    void shouldUpdateStatusAndPublishMessage() {
        DeviceEntity device = new DeviceEntity();
        device.setStatus(DeviceStatus.ONLINE);
        when(deviceRepository.findById("DEVICE-001")).thenReturn(Optional.of(device));
        deviceStatusService.updateStatus("DEVICE-001", DeviceStatus.CRITICAL, "Critical alarm");
        verify(deviceRepository).save(device);
        assertEquals(DeviceStatus.CRITICAL, device.getStatus());
        assertNotNull(device.getLastUpdated());
        ArgumentCaptor<DeviceStatusMessage> captor = ArgumentCaptor.forClass(DeviceStatusMessage.class);
        verify(deviceStatusPublisher).publish(captor.capture());
        DeviceStatusMessage message = captor.getValue();
        assertEquals("DEVICE-001", message.deviceId());
        assertEquals(DeviceStatus.CRITICAL, message.status());
        assertEquals("Critical alarm", message.reason());
    }
}