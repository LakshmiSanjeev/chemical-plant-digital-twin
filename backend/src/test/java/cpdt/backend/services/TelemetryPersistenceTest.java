package cpdt.backend.services;

import cpdt.backend.entities.TelemetryEntity;
import cpdt.backend.repositories.TelemetryRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TelemetryPersistenceTest {

    @Mock
    private TelemetryRepository telemetryRepository;

    @InjectMocks
    private TelemetryPersistenceService telemetryPersistenceService;

    @Test
    void shouldSaveTelemetryPacket() {
        long timestamp = System.currentTimeMillis();
        TelemetryPacket packet = new TelemetryPacket(
                "DEVICE-001",
                "Temperature Sensor",
                DeviceType.TEMPERATURE_SENSOR,
                DeviceStatus.ONLINE,
                "LOC-001",
                "Reactor",
                ProcessArea.REACTOR_SECTION,
                System.currentTimeMillis(),
                MeasurementType.TEMPERATURE,
                72.8
        );
        telemetryPersistenceService.save(packet);
        ArgumentCaptor<TelemetryEntity> captor = ArgumentCaptor.forClass(TelemetryEntity.class);
        verify(telemetryRepository).save(captor.capture());
        TelemetryEntity entity = captor.getValue();
        assertEquals(packet.deviceId(), entity.getDeviceId());
        assertEquals(packet.deviceName(), entity.getDeviceName());
        assertEquals(packet.deviceType(), entity.getDeviceType());
        assertEquals(packet.status(), entity.getStatus());
        assertEquals(packet.locationId(), entity.getLocationId());
        assertEquals(packet.locationName(), entity.getLocationName());
        assertEquals(packet.processArea(), entity.getProcessArea());
        assertEquals(packet.measurementType(), entity.getMeasurementType());
        assertEquals(packet.value(), entity.getValue());
        assertEquals(Instant.ofEpochMilli(timestamp), entity.getTimestamp());
    }
}