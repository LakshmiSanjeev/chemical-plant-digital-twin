package cpdt.backend.controllers;

import cpdt.backend.entities.TelemetryEntity;
import cpdt.backend.services.TelemetryQueryService;
import cpdt.common.enums.ProcessArea;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Exposes REST endpoints for querying historical telemetry data.
 *
 * <p>This controller provides access to telemetry records collected by
 * the backend. Clients can retrieve telemetry history for individual
 * devices or query measurements within a specific process area over a
 * given time interval.
 *
 * <p>The controller delegates all data retrieval operations to the
 * telemetry query service.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
@RestController
@RequestMapping("/api/telemetry")
@RequiredArgsConstructor
public class TelemetryController {

    private final TelemetryQueryService telemetryQueryService;

    /**
     * Retrieves telemetry history for a specific device.
     *
     * @param deviceId the unique identifier of the device
     * @param limit the maximum number of telemetry records to return
     * @return a list containing historical telemetry measurements
     */
    @GetMapping("/{deviceId}")
    public List<TelemetryEntity> getTelemetryHistory(@PathVariable String deviceId, @RequestParam(defaultValue = "100") int limit) {
        return telemetryQueryService.getDeviceHistory(deviceId, limit);
    }
    /**
     * Retrieves telemetry history for a process area.
     *
     * @param processArea the process area to query
     * @param since the earliest timestamp to include
     * @param limit the maximum number of telemetry records to return
     * @return a list containing telemetry measurements for the specified area
     */
    @GetMapping("/area/{processArea}")
    public List<TelemetryEntity> getAreaTelemetry(@PathVariable ProcessArea processArea, @RequestParam long since, @RequestParam(defaultValue = "100") int limit) {
        return telemetryQueryService.getAreaHistory(processArea, since, limit);
    }
}