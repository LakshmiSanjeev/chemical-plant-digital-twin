package cpdt.backend.repositories;

import cpdt.backend.entities.AlarmThresholdEntity;
import cpdt.common.enums.MeasurementType;
import cpdt.common.enums.ProcessArea;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for accessing alarm threshold configurations.
 *
 * <p>Provides database operations for retrieving and managing alarm
 * threshold settings used during telemetry evaluation.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
public interface AlarmThresholdRepository extends JpaRepository<AlarmThresholdEntity, Long> {
    Optional<AlarmThresholdEntity> findByProcessAreaAndMeasurementType(
            ProcessArea processArea, MeasurementType measurementType);
}