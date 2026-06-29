package cpdt.backend.repositories;

import cpdt.backend.entities.TelemetryEntity;
import cpdt.common.enums.ProcessArea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface TelemetryRepository extends JpaRepository<TelemetryEntity, Long> {

    Page<TelemetryEntity> findByDeviceIdOrderByTimestampDesc(
            String deviceId,
            Pageable pageable
    );

    Page<TelemetryEntity> findByDeviceIdAndTimestampBetweenOrderByTimestampDesc(
            String deviceId,
            Instant from,
            Instant to,
            Pageable pageable
    );

    Page<TelemetryEntity> findByProcessAreaAndTimestampAfterOrderByTimestampDesc(
            ProcessArea processArea,
            Instant since,
            Pageable pageable
    );
}