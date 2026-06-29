package cpdt.backend.services;

import cpdt.backend.exception.InvalidTelemetryException;
import cpdt.backend.twin.TwinStateStore;
import cpdt.common.dto.TelemetryPacket;
import cpdt.common.utils.TelemetrySerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelemetryIngestionService {

    private final DevicePersistenceService devicePersistenceService;
    private final TelemetryPersistenceService telemetryPersistenceService;
    private final TwinStateStore twinStateStore;

    private final AlertEvaluationService alertEvaluationService;
    private final AlertPersistenceService alertPersistenceService;

    public TelemetryPacket ingest(byte[] payload) {

        if (payload == null || payload.length == 0) {
            throw new InvalidTelemetryException("Received empty MQTT payload.");
        }

        TelemetryPacket packet;

        try {
            packet = TelemetrySerializer.fromJson(payload, TelemetryPacket.class);
        } catch (RuntimeException ex) {
            throw new InvalidTelemetryException(
                    "Failed to deserialize telemetry payload.",
                    ex
            );
        }

        validate(packet);

        devicePersistenceService.registerOrUpdate(packet);
        telemetryPersistenceService.save(packet);

        twinStateStore.update(packet);

        alertEvaluationService.evaluate(packet).ifPresent(alertPersistenceService::save);

        return packet;
    }

    private void validate(TelemetryPacket packet) {

        if (packet == null) {
            throw new InvalidTelemetryException("Telemetry packet is null.");
        }

        if (packet.deviceId() == null || packet.deviceId().isBlank()) {
            throw new InvalidTelemetryException("Device ID is missing.");
        }

        if (packet.deviceName() == null || packet.deviceName().isBlank()) {
            throw new InvalidTelemetryException("Device name is missing.");
        }

        if (packet.deviceType() == null) {
            throw new InvalidTelemetryException("Device type is missing.");
        }

        if (packet.locationId() == null || packet.locationId().isBlank()) {
            throw new InvalidTelemetryException("Location ID is missing.");
        }

        if (packet.locationName() == null || packet.locationName().isBlank()) {
            throw new InvalidTelemetryException("Location name is missing.");
        }

        if (packet.processArea() == null) {
            throw new InvalidTelemetryException("Process area is missing.");
        }

        if (packet.measurementType() == null) {
            throw new InvalidTelemetryException("Measurement type is missing.");
        }

        if (!Double.isFinite(packet.value())) {
            throw new InvalidTelemetryException("Telemetry value is invalid.");
        }

        if (packet.status() == null) {
            throw new InvalidTelemetryException("Device status is missing.");
        }

        if (packet.timestamp() <= 0) {
            throw new InvalidTelemetryException("Timestamp is invalid.");
        }
    }
}