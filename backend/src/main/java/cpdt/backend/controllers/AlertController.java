package cpdt.backend.controllers;

import cpdt.backend.entities.AlertEntity;
import cpdt.backend.services.AlertFetch;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Exposes REST endpoints for managing process alarms.
 *
 * <p>This controller provides endpoints for retrieving active and historical
 * alarms as well as acknowledging existing alarms.
 *
 * <p>The controller serves as the primary interface for monitoring alarm
 * conditions within the Digital Twin backend.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertFetch alert;

    /**
     * Retrieves a paginated list of all recorded alerts.
     *
     * @param pageable pagination information including page number and size
     * @return a page containing alert records
     */
    @GetMapping
    public Page<AlertEntity> getAllAlerts(Pageable pageable) {
        return alert.getAllAlerts(pageable);
    }
    /**
     * Retrieves all currently active alerts.
     *
     * @return a list containing active alerts
     */
    @GetMapping("/active")
    public List<AlertEntity> getActiveAlerts() {
        return alert.getActiveAlerts();
    }
    /**
     * Acknowledges an active alert.
     *
     * @param alertId the unique identifier of the alert to acknowledge
     */
    @PostMapping("/{alertId}/acknowledge")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void acknowledge(@PathVariable String alertId) {
        alert.acknowledge(alertId);
    }
}