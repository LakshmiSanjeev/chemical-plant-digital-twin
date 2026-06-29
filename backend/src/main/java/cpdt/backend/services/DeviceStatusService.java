package cpdt.backend.services;

import cpdt.backend.entities.DeviceEntity;
import cpdt.backend.mqtt.DeviceStatusPublisher;
import cpdt.backend.repositories.DeviceRepository;
import cpdt.common.dto.DeviceStatusMessage;
import cpdt.common.enums.DeviceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class DeviceStatusService {

    private final DeviceRepository deviceRepository;
    private final DeviceStatusPublisher deviceStatusPublisher;

    public void updateStatus(
            String deviceId,
            DeviceStatus newStatus,
            String reason
    ) {

        DeviceEntity device = deviceRepository.findById(deviceId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Device not found: " + deviceId));

        if (device.getStatus() == newStatus) {
            return;
        }

        device.setStatus(newStatus);
        device.setLastUpdated(Instant.now());

        deviceRepository.save(device);

        DeviceStatusMessage message =
                new DeviceStatusMessage(
                        deviceId,
                        newStatus,
                        reason,
                        System.currentTimeMillis()
                );

        deviceStatusPublisher.publish(message);
    }
}