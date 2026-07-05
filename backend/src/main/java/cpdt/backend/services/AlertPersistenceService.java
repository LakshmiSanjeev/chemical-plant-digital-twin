package cpdt.backend.services;

import cpdt.backend.entities.AlertEntity;
import cpdt.backend.repositories.AlertRepository;
import cpdt.common.dto.AlertMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Persists generated alerts to the database.
 *
 * <p>This service converts alert messages produced by the alarm
 * evaluation process into persistent entities and stores them using
 * the alert repository.
 *
 * <p>It separates alert generation from database persistence within
 * the backend architecture.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class AlertPersistenceService {

    private final AlertRepository alertRepository;

    public void save(AlertMessage alertMessage) {
        AlertEntity entity = AlertEntity.builder()
                .alertId(alertMessage.alertId())
                .deviceId(alertMessage.deviceId())
                .severity(alertMessage.severity())
                .message(alertMessage.message())
                .timestamp(Instant.ofEpochMilli(alertMessage.timestamp()))
                .build();
        alertRepository.save(entity);
    }
}