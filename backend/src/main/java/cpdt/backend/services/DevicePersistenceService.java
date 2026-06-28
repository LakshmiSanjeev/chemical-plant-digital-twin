package cpdt.backend.services;

import cpdt.backend.entities.DeviceEntity;
import cpdt.backend.repositories.DeviceRepository;
import cpdt.common.dto.TelemetryPacket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DevicePersistenceService {

    private final DeviceRepository deviceRepository;

    public void registerOrUpdate(TelemetryPacket packet) {
        DeviceEntity device = deviceRepository.findById(packet.deviceId())
                .orElseGet(() -> DeviceEntity.builder().deviceId(packet.deviceId()).build());
        device.setName(packet.deviceName());
        device.setDeviceType(packet.deviceType());
        device.setProcessArea(packet.processArea());
        device.setStatus(packet.status());
        device.setLastUpdated(Instant.ofEpochMilli(packet.timestamp()));
        deviceRepository.save(device);
    }

    public List<DeviceEntity> getAllDevices() {
        return deviceRepository.findAll();
    }

    public Optional<DeviceEntity> getDevice(String deviceId) {
        return deviceRepository.findById(deviceId);
    }
}