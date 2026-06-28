package cpdt.backend.repositories;

import cpdt.backend.entities.AlarmThresholdEntity;
import cpdt.common.enums.MeasurementType;
import cpdt.common.enums.ProcessArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlarmThresholdRepository
        extends JpaRepository<AlarmThresholdEntity, Long> {

    Optional<AlarmThresholdEntity> findByProcessAreaAndMeasurementType(
            ProcessArea processArea,
            MeasurementType measurementType
    );

}