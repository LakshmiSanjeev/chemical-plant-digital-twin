package cpdt.backend.repositories;

import cpdt.backend.entities.TelemetryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelemetryRepository extends JpaRepository<TelemetryEntity, Long> {
    Page<TelemetryEntity> findByDeviceIdOrderByTimestampDesc(String deviceId, Pageable pageable);
}