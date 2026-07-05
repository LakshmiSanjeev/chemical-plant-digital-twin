package cpdt.backend.services;

import cpdt.backend.entities.DeviceEntity;
import cpdt.backend.mqtt.DeviceStatusPublisher;
import cpdt.backend.repositories.DeviceRepository;
import cpdt.common.dto.DeviceStatusMessage;
import cpdt.common.enums.DeviceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Manages operational status updates for registered devices.
 *
 * <p>This service updates the current status of devices based on alarm
 * evaluation results and publishes status change notifications through
 * MQTT. Status updates are persisted to the database before being
 * propagated to external systems.
 *
 * <p>The service ensures that status notifications are published only
 * when a device's operational state changes.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class DeviceStatusService {

    private final DeviceRepository deviceRepository;
    private final DeviceStatusPublisher deviceStatusPublisher;

    public void updateStatus(String deviceId, DeviceStatus newStatus, String reason) {
        DeviceEntity device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found: " + deviceId));

        if (device.getStatus() == newStatus) {
            return;
        }
        device.setStatus(newStatus);
        device.setLastUpdated(Instant.now());
        deviceRepository.save(device);
        DeviceStatusMessage message = new DeviceStatusMessage(deviceId, newStatus, reason, System.currentTimeMillis());
        deviceStatusPublisher.publish(message);
    }
}