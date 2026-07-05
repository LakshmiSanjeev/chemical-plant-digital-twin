package cpdt.backend.services;

import cpdt.backend.exception.InvalidTelemetryException;
import cpdt.backend.twin.TwinStateStore;
import cpdt.common.dto.AlertMessage;
import cpdt.common.dto.TelemetryPacket;
import cpdt.common.enums.DeviceStatus;
import cpdt.common.enums.DeviceType;
import cpdt.common.enums.MeasurementType;
import cpdt.common.enums.ProcessArea;
import cpdt.common.utils.TelemetrySerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelemetryIngestionTest {

    @Mock
    private DevicePersistenceService devicePersistenceService;

    @Mock
    private TelemetryPersistenceService telemetryPersistenceService;

    @Mock
    private TwinStateStore twinStateStore;

    @Mock
    private AlertEvaluationService alertEvaluationService;

    @Mock
    private AlertPersistenceService alertPersistenceService;

    @InjectMocks
    private TelemetryIngestionService telemetryIngestionService;

    private TelemetryPacket packet;

    @BeforeEach
    void setUp() {
        packet = new TelemetryPacket(
                "TEMP-001",
                "Temperature Sensor",
                DeviceType.TEMPERATURE_SENSOR,
                DeviceStatus.ONLINE,
                "LOC-001",
                "Reactor Zone",
                ProcessArea.REACTOR_SECTION,
                System.currentTimeMillis(),
                MeasurementType.TEMPERATURE,
                65.5
        );
    }

    private byte[] validPayload() {
        return TelemetrySerializer.toJson(packet);
    }

    @Test
    void shouldIngestValidTelemetryPacket() {
        when(alertEvaluationService.evaluate(packet)).thenReturn(Optional.empty());
        TelemetryPacket result = telemetryIngestionService.ingest(validPayload());
        assertEquals(packet, result);
        verify(devicePersistenceService).registerOrUpdate(packet);
        verify(telemetryPersistenceService).save(packet);
        verify(twinStateStore).update(packet);
        verify(alertEvaluationService).evaluate(packet);
        verify(alertPersistenceService, never()).save(any());
    }

    @Test
    void shouldPersistAlertWhenGenerated() {
        AlertMessage alert = mock(AlertMessage.class);
        when(alertEvaluationService.evaluate(packet)).thenReturn(Optional.of(alert));
        telemetryIngestionService.ingest(validPayload());
        verify(alertPersistenceService).save(alert);
    }

    @Test
    void shouldThrowWhenPayloadIsNull() {
        assertThrows(InvalidTelemetryException.class, () -> telemetryIngestionService.ingest(null));
        verifyNoInteractions(devicePersistenceService);
        verifyNoInteractions(telemetryPersistenceService);
        verifyNoInteractions(twinStateStore);
        verifyNoInteractions(alertEvaluationService);
        verifyNoInteractions(alertPersistenceService);
    }

    @Test
    void shouldThrowWhenPayloadIsEmpty() {
        assertThrows(InvalidTelemetryException.class, () -> telemetryIngestionService.ingest(new byte[0]));
        verifyNoInteractions(devicePersistenceService);
        verifyNoInteractions(telemetryPersistenceService);
        verifyNoInteractions(twinStateStore);
        verifyNoInteractions(alertEvaluationService);
        verifyNoInteractions(alertPersistenceService);
    }

    @Test
    void shouldThrowWhenPayloadCannotBeDeserialized() {
        byte[] payload = "Not JSON".getBytes();
        assertThrows(InvalidTelemetryException.class, () -> telemetryIngestionService.ingest(payload));
        verifyNoInteractions(devicePersistenceService);
        verifyNoInteractions(telemetryPersistenceService);
        verifyNoInteractions(twinStateStore);
        verifyNoInteractions(alertEvaluationService);
        verifyNoInteractions(alertPersistenceService);
    }

    @Test
    void shouldThrowWhenDeviceIdIsMissing() {
        packet = new TelemetryPacket(
                "",
                packet.deviceName(),
                packet.deviceType(),
                packet.status(),
                packet.locationId(),
                packet.locationName(),
                packet.processArea(),
                packet.timestamp(),
                packet.measurementType(),
                packet.value()
        );
        assertThrows(InvalidTelemetryException.class, () -> telemetryIngestionService.ingest(TelemetrySerializer.toJson(packet)));
    }

    @Test
    void shouldThrowWhenDeviceNameIsMissing() {
        packet = new TelemetryPacket(
                packet.deviceId(),
                "",
                packet.deviceType(),
                packet.status(),
                packet.locationId(),
                packet.locationName(),
                packet.processArea(),
                packet.timestamp(),
                packet.measurementType(),
                packet.value()
        );
        assertThrows(InvalidTelemetryException.class, () -> telemetryIngestionService.ingest(TelemetrySerializer.toJson(packet)));
    }

    @Test
    void shouldThrowWhenDeviceTypeIsMissing() {
        packet = new TelemetryPacket(
                packet.deviceId(),
                packet.deviceName(),
                null,
                packet.status(),
                packet.locationId(),
                packet.locationName(),
                packet.processArea(),
                packet.timestamp(),
                packet.measurementType(),
                packet.value()
        );
        assertThrows(InvalidTelemetryException.class, () -> telemetryIngestionService.ingest(TelemetrySerializer.toJson(packet)));
    }

    @Test
    void shouldThrowWhenLocationIdIsMissing() {
        packet = new TelemetryPacket(
                packet.deviceId(),
                packet.deviceName(),
                packet.deviceType(),
                packet.status(),
                "",
                packet.locationName(),
                packet.processArea(),
                packet.timestamp(),
                packet.measurementType(),
                packet.value()
        );
        assertThrows(InvalidTelemetryException.class, () -> telemetryIngestionService.ingest(TelemetrySerializer.toJson(packet)));
    }

    @Test
    void shouldThrowWhenLocationNameIsMissing() {
        packet = new TelemetryPacket(
                packet.deviceId(),
                packet.deviceName(),
                packet.deviceType(),
                packet.status(),
                packet.locationId(),
                "",
                packet.processArea(),
                packet.timestamp(),
                packet.measurementType(),
                packet.value()
        );
        assertThrows(InvalidTelemetryException.class, () -> telemetryIngestionService.ingest(TelemetrySerializer.toJson(packet)));
    }

    @Test
    void shouldThrowWhenProcessAreaIsMissing() {
        packet = new TelemetryPacket(
                packet.deviceId(),
                packet.deviceName(),
                packet.deviceType(),
                packet.status(),
                packet.locationId(),
                packet.locationName(),
                null,
                packet.timestamp(),
                packet.measurementType(),
                packet.value()
        );
        assertThrows(InvalidTelemetryException.class, () -> telemetryIngestionService.ingest(TelemetrySerializer.toJson(packet)));
    }

    @Test
    void shouldThrowWhenMeasurementTypeIsMissing() {
        packet = new TelemetryPacket(
                packet.deviceId(),
                packet.deviceName(),
                packet.deviceType(),
                packet.status(),
                packet.locationId(),
                packet.locationName(),
                packet.processArea(),
                packet.timestamp(),
                null,
                packet.value()
        );
        assertThrows(InvalidTelemetryException.class, () -> telemetryIngestionService.ingest(TelemetrySerializer.toJson(packet)));
    }

    @Test
    void shouldThrowWhenTelemetryValueIsNaN() {
        packet = new TelemetryPacket(
                packet.deviceId(),
                packet.deviceName(),
                packet.deviceType(),
                packet.status(),
                packet.locationId(),
                packet.locationName(),
                packet.processArea(),
                packet.timestamp(),
                packet.measurementType(),
                Double.NaN
        );
        assertThrows(InvalidTelemetryException.class, () -> telemetryIngestionService.ingest(TelemetrySerializer.toJson(packet)));
    }

    @Test
    void shouldThrowWhenDeviceStatusIsMissing() {
        packet = new TelemetryPacket(
                packet.deviceId(),
                packet.deviceName(),
                packet.deviceType(),
                null,
                packet.locationId(),
                packet.locationName(),
                packet.processArea(),
                packet.timestamp(),
                packet.measurementType(),
                packet.value()
        );
        assertThrows(InvalidTelemetryException.class, () -> telemetryIngestionService.ingest(TelemetrySerializer.toJson(packet)));
    }

    @Test
    void shouldThrowWhenTimestampIsZero() {
        packet = new TelemetryPacket(
                packet.deviceId(),
                packet.deviceName(),
                packet.deviceType(),
                packet.status(),
                packet.locationId(),
                packet.locationName(),
                packet.processArea(),
                0L,
                packet.measurementType(),
                packet.value()
        );
        assertThrows(InvalidTelemetryException.class, () -> telemetryIngestionService.ingest(TelemetrySerializer.toJson(packet)));
    }

    @Test
    void shouldThrowWhenTelemetryPacketIsNull() {
        assertThrows(InvalidTelemetryException.class, () -> telemetryIngestionService.ingest(TelemetrySerializer.toJson(null)));
        verifyNoInteractions(devicePersistenceService);
        verifyNoInteractions(telemetryPersistenceService);
        verifyNoInteractions(twinStateStore);
        verifyNoInteractions(alertEvaluationService);
        verifyNoInteractions(alertPersistenceService);
    }
}