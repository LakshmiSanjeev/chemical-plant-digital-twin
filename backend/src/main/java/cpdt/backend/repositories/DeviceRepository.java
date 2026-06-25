package cpdt.backend.repositories;

import cpdt.backend.entities.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<DeviceEntity, String> { }