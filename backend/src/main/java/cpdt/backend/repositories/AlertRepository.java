package cpdt.backend.repositories;

import cpdt.backend.entities.AlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<AlertEntity, String> {
    List<AlertEntity> findByAcknowledgedFalse();
}