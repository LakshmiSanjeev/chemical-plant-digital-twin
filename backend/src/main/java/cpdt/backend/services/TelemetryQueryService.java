package cpdt.backend.services;

import cpdt.backend.entities.TelemetryEntity;
import cpdt.backend.repositories.TelemetryRepository;
import cpdt.common.enums.ProcessArea;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TelemetryQueryService {

    private final TelemetryRepository telemetryRepository;

    public List<TelemetryEntity> getDeviceHistory(String deviceId, int limit) {

        return telemetryRepository
                .findByDeviceIdOrderByTimestampDesc(
                        deviceId,
                        PageRequest.of(0, limit)
                )
                .getContent();
    }

    public List<TelemetryEntity> getAreaHistory(
            ProcessArea processArea,
            long sinceEpochMs,
            int limit
    ) {

        return telemetryRepository
                .findByProcessAreaAndTimestampAfterOrderByTimestampDesc(
                        processArea,
                        Instant.ofEpochMilli(sinceEpochMs),
                        PageRequest.of(0, limit)
                )
                .getContent();
    }
}