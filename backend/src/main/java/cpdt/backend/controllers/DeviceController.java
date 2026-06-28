package cpdt.backend.controllers;

import cpdt.backend.entities.DeviceEntity;
import cpdt.backend.repositories.DeviceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceRepository deviceRepository;

    public DeviceController(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @GetMapping
    public List<DeviceEntity> getAllDevices() {
        return deviceRepository.findAll();
    }

    @GetMapping("/{deviceId}")
    public ResponseEntity<DeviceEntity> getDevice(@PathVariable String deviceId) {
        return deviceRepository.findById(deviceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}