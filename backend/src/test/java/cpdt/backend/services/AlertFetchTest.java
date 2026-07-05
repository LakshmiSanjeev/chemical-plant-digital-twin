package cpdt.backend.services;

import cpdt.backend.entities.AlertEntity;
import cpdt.backend.repositories.AlertRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertFetchTest {

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private AlertFetch alertFetch;

    @Test
    void shouldReturnAllAlerts() {
        Page<AlertEntity> page = new PageImpl<>(List.of(new AlertEntity()), PageRequest.of(0,10), 1);
        when(alertRepository.findAll(any(PageRequest.class))).thenReturn(page);
        assertEquals(page, alertFetch.getAllAlerts(PageRequest.of(0,10)));
        verify(alertRepository).findAll(any(PageRequest.class));
    }

    @Test
    void shouldReturnActiveAlerts() {
        List<AlertEntity> alerts = List.of(new AlertEntity());
        when(alertRepository.findByAcknowledgedFalse()).thenReturn(alerts);
        assertEquals(alerts, alertFetch.getActiveAlerts());
        verify(alertRepository).findByAcknowledgedFalse();
    }

    @Test
    void shouldAcknowledgeAlert() {
        AlertEntity alert = new AlertEntity();
        alert.setAcknowledged(false);
        when(alertRepository.findById("ALERT-001")).thenReturn(Optional.of(alert));
        alertFetch.acknowledge("ALERT-001");
        assertTrue(alert.isAcknowledged());
        verify(alertRepository).save(alert);
    }

    @Test
    void shouldThrowWhenAlertDoesNotExist() {
        when(alertRepository.findById("UNKNOWN")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> alertFetch.acknowledge("UNKNOWN"));
        verify(alertRepository, never()).save(any());
    }
}