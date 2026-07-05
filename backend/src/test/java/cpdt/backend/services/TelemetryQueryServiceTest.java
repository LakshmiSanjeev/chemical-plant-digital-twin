package cpdt.backend.services;

import cpdt.backend.entities.TelemetryEntity;
import cpdt.backend.repositories.TelemetryRepository;
import cpdt.common.enums.ProcessArea;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TelemetryQueryServiceTest {

    @Mock
    private TelemetryRepository telemetryRepository;

    @InjectMocks
    private TelemetryQueryService telemetryQueryService;

    @Test
    void shouldReturnDeviceHistory() {
        List<TelemetryEntity> telemetry = List.of(new TelemetryEntity(), new TelemetryEntity());
        when(telemetryRepository.findByDeviceIdOrderByTimestampDesc(eq("DEVICE-001"), any(Pageable.class))).thenReturn(new PageImpl<>(telemetry));
        List<TelemetryEntity> result = telemetryQueryService.getDeviceHistory("DEVICE-001", 10);
        assertEquals(2, result.size());
        verify(telemetryRepository).findByDeviceIdOrderByTimestampDesc(eq("DEVICE-001"), any(Pageable.class));
    }

    @Test
    void shouldReturnAreaHistory() {
        List<TelemetryEntity> telemetry = List.of(new TelemetryEntity());
        when(telemetryRepository.findByProcessAreaAndTimestampAfterOrderByTimestampDesc(eq(ProcessArea.REACTOR_SECTION), any(Instant.class), any(Pageable.class))).thenReturn(new PageImpl<>(telemetry));
        List<TelemetryEntity> result = telemetryQueryService.getAreaHistory(ProcessArea.REACTOR_SECTION, System.currentTimeMillis(), 20);
        assertEquals(1, result.size());
        verify(telemetryRepository).findByProcessAreaAndTimestampAfterOrderByTimestampDesc(
                eq(ProcessArea.REACTOR_SECTION), any(Instant.class), any(Pageable.class));
    }
}