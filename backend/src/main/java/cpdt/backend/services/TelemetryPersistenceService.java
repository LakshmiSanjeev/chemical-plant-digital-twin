package cpdt.backend.services;

import cpdt.backend.entities.TelemetryEntity;
import cpdt.backend.repositories.TelemetryRepository;
import cpdt.common.dto.TelemetryPacket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

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