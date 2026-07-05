package cpdt.backend.services;

import cpdt.backend.entities.DeviceEntity;
import cpdt.backend.repositories.DeviceRepository;
import cpdt.common.dto.TelemetryPacket;
import cpdt.common.enums.DeviceStatus;
import cpdt.common.enums.DeviceType;
import cpdt.common.enums.MeasurementType;
import cpdt.common.enums.ProcessArea;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DevicePersistenceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DevicePersistenceService devicePersistenceService;

    private TelemetryPacket createPacket() {
        return new TelemetryPacket(
                "DEVICE-001",
                "Temperature Sensor",
                DeviceType.TEMPERATURE_SENSOR,
                DeviceStatus.ONLINE,
                "LOC-001",
                "Reactor",
                ProcessArea.REACTOR_SECTION,
                System.currentTimeMillis(),
                MeasurementType.TEMPERATURE,
                75.5
        );
    }

    @Test
    void shouldCreateNewDevice() {
        TelemetryPacket packet = createPacket();
        when(deviceRepository.findById(packet.deviceId())).thenReturn(Optional.empty());
        devicePersistenceService.registerOrUpdate(packet);
        ArgumentCaptor<DeviceEntity> captor = ArgumentCaptor.forClass(DeviceEntity.class);
        verify(deviceRepository).save(captor.capture());
        DeviceEntity entity = captor.getValue();
        assertEquals(packet.deviceId(), entity.getDeviceId());
        assertEquals(packet.deviceName(), entity.getName());
        assertEquals(packet.deviceType(), entity.getDeviceType());
        assertEquals(packet.processArea(), entity.getProcessArea());
        assertEquals(packet.status(), entity.getStatus());
        assertEquals(Instant.ofEpochMilli(packet.timestamp()), entity.getLastUpdated());
    }

    @Test
    void shouldUpdateExistingDevice() {
        TelemetryPacket packet = createPacket();
        DeviceEntity existing = DeviceEntity.builder().deviceId(packet.deviceId()).build();
        when(deviceRepository.findById(packet.deviceId())).thenReturn(Optional.of(existing));
        devicePersistenceService.registerOrUpdate(packet);
        verify(deviceRepository).save(existing);
        assertEquals(packet.deviceName(), existing.getName());
        assertEquals(packet.deviceType(), existing.getDeviceType());
        assertEquals(packet.processArea(), existing.getProcessArea());
        assertEquals(packet.status(), existing.getStatus());
    }

    @Test
    void shouldReturnAllDevices() {
        List<DeviceEntity> devices = List.of(new DeviceEntity());
        when(deviceRepository.findAll()).thenReturn(devices);
        assertEquals(devices, devicePersistenceService.getAllDevices());
        verify(deviceRepository).findAll();
    }

    @Test
    void shouldReturnDeviceById() {
        DeviceEntity device = new DeviceEntity();
        when(deviceRepository.findById("DEVICE-001")).thenReturn(Optional.of(device));
        Optional<DeviceEntity> result = devicePersistenceService.getDevice("DEVICE-001");
        assertTrue(result.isPresent());
        assertEquals(device, result.get());
        verify(deviceRepository).findById("DEVICE-001");
    }
}