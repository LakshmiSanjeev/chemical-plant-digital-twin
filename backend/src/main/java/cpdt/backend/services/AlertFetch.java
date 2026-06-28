package cpdt.backend.services;

import cpdt.backend.entities.AlertEntity;
import cpdt.backend.repositories.AlertRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertFetch {

    private final AlertRepository alertRepository;

    public Page<AlertEntity> getAllAlerts(Pageable pageable) {
        return alertRepository.findAll(pageable);
    }

    public List<AlertEntity> getActiveAlerts() {
        return alertRepository.findByAcknowledgedFalse();
    }

    public void acknowledge(String alertId) {

        AlertEntity alert = alertRepository.findById(alertId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Alert not found: " + alertId));

        alert.setAcknowledged(true);

        alertRepository.save(alert);
    }
}