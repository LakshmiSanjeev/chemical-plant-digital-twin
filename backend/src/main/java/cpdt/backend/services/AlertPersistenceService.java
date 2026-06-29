package cpdt.backend.services;

import cpdt.backend.entities.AlertEntity;
import cpdt.backend.repositories.AlertRepository;
import cpdt.common.dto.AlertMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

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