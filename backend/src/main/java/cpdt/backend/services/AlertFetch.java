package cpdt.backend.services;

import cpdt.backend.entities.AlertEntity;
import cpdt.backend.repositories.AlertRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Provides retrieval and management operations for persisted alerts.
 *
 * <p>This service retrieves historical and active alerts from the
 * database and supports acknowledging existing alerts through the
 * backend API.
 *
 * <p>It acts as the business layer between the alert repository and
 * the REST controllers.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class AlertFetch {

    private final AlertRepository alertRepository;
    /**
     * Retrieves a paginated list of all persisted alerts.
     *
     * @param pageable pagination information
     * @return a page containing alert records
     */
    public Page<AlertEntity> getAllAlerts(Pageable pageable) {
        return alertRepository.findAll(pageable);
    }
    /**
     * Retrieves all active, unacknowledged alerts.
     *
     * @return a list containing active alerts
     */
    public List<AlertEntity> getActiveAlerts() {
        return alertRepository.findByAcknowledgedFalse();
    }
    /**
     * Marks an alert as acknowledged.
     *
     * @param alertId the unique identifier of the alert
     * @throws EntityNotFoundException if the alert does not exist
     */
    public void acknowledge(String alertId) {
        AlertEntity alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new EntityNotFoundException("Alert not found: " + alertId));
        alert.setAcknowledged(true);
        alertRepository.save(alert);
    }
}