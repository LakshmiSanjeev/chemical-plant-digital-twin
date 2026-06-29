package cpdt.backend.controllers;

import cpdt.backend.entities.TelemetryEntity;
import cpdt.backend.services.TelemetryQueryService;
import cpdt.common.enums.ProcessArea;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/telemetry")
@RequiredArgsConstructor
public class TelemetryController {

    private final TelemetryQueryService telemetryQueryService;

    @GetMapping("/{deviceId}")
    public List<TelemetryEntity> getTelemetryHistory(
            @PathVariable String deviceId,
            @RequestParam(defaultValue = "100") int limit
    ) {

        return telemetryQueryService.getDeviceHistory(deviceId, limit);
    }

    @GetMapping("/area/{processArea}")
    public List<TelemetryEntity> getAreaTelemetry(
            @PathVariable ProcessArea processArea,
            @RequestParam long since,
            @RequestParam(defaultValue = "100") int limit
    ) {

        return telemetryQueryService.getAreaHistory(
                processArea,
                since,
                limit
        );
    }
}