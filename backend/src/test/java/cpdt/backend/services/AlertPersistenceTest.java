package cpdt.backend.services;

import cpdt.backend.entities.AlertEntity;
import cpdt.backend.repositories.AlertRepository;
import cpdt.common.dto.AlertMessage;
import cpdt.common.enums.AlertSeverity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AlertPersistenceTest {

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private AlertPersistenceService alertPersistenceService;

    @Test
    void shouldSaveAlertMessageAsEntity() {
        long timestamp = System.currentTimeMillis();
        AlertMessage message = new AlertMessage("ALERT-001", "DEVICE-001", AlertSeverity.CRITICAL_HIGH, "Critical temperature detected", timestamp);
        alertPersistenceService.save(message);
        ArgumentCaptor<AlertEntity> captor = ArgumentCaptor.forClass(AlertEntity.class);
        verify(alertRepository).save(captor.capture());
        AlertEntity entity = captor.getValue();
        assertEquals(message.alertId(), entity.getAlertId());
        assertEquals(message.deviceId(), entity.getDeviceId());
        assertEquals(message.severity(), entity.getSeverity());
        assertEquals(message.message(), entity.getMessage());
        assertEquals(Instant.ofEpochMilli(timestamp), entity.getTimestamp());
    }
}