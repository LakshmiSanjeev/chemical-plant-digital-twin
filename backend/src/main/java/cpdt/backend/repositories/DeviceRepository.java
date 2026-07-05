package cpdt.backend.repositories;

import cpdt.backend.entities.DeviceEntity;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for accessing registered device information.
 *
 * <p>Provides database operations for storing and retrieving device
 * metadata maintained by the Digital Twin backend.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
public interface DeviceRepository extends JpaRepository<DeviceEntity, String> { }