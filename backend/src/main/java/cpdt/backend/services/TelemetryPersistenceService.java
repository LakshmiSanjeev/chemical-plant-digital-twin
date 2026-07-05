package cpdt.backend.services;

import cpdt.backend.entities.TelemetryEntity;
import cpdt.backend.repositories.TelemetryRepository;
import cpdt.common.dto.TelemetryPacket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Persists telemetry measurements received by the backend.
 *
 * <p>This service converts incoming telemetry packets into persistent
 * entities and stores them in the telemetry database. Each telemetry
 * measurement is preserved to support historical analysis and Digital
 * Twin queries.
 *
 * <p>The service isolates persistence logic from the telemetry ingestion
 * workflow.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class TelemetryPersistenceService {

    private final TelemetryRepository telemetryRepository;

    public void save(TelemetryPacket packet) {
        TelemetryEntity telemetry = TelemetryEntity.builder()
                .deviceId(packet.deviceId())
                .deviceName(packet.deviceName())
                .deviceType(packet.deviceType())
                .status(packet.status())
                .locationId(packet.locationId())
                .locationName(packet.locationName())
                .processArea(packet.processArea())
                .measurementType(packet.measurementType())
                .value(packet.value())
                .timestamp(Instant.ofEpochMilli(packet.timestamp()))
                .build();
        telemetryRepository.save(telemetry);
    }
}