package cpdt.backend.services;

import cpdt.backend.entities.TelemetryEntity;
import cpdt.backend.repositories.TelemetryRepository;
import cpdt.common.enums.ProcessArea;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Provides query operations for historical telemetry data.
 *
 * <p>This service retrieves telemetry measurements stored by the backend,
 * allowing clients to query device history or telemetry associated with
 * a specific process area over a given time period.
 *
 * <p>It acts as the business layer between the telemetry repository and
 * the REST controllers.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class TelemetryQueryService {

    private final TelemetryRepository telemetryRepository;

    public List<TelemetryEntity> getDeviceHistory(String deviceId, int limit) {
        return telemetryRepository
                .findByDeviceIdOrderByTimestampDesc(deviceId, PageRequest.of(0, limit))
                .getContent();
    }

    public List<TelemetryEntity> getAreaHistory(ProcessArea processArea, long sinceEpochMs, int limit) {
        return telemetryRepository
                .findByProcessAreaAndTimestampAfterOrderByTimestampDesc(processArea, Instant.ofEpochMilli(sinceEpochMs), PageRequest.of(0, limit))
                .getContent();
    }
}