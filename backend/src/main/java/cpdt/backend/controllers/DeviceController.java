package cpdt.backend.controllers;

import cpdt.backend.entities.DeviceEntity;
import cpdt.backend.repositories.DeviceRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Exposes REST endpoints for accessing registered device information.
 *
 * <p>This controller provides operations for retrieving all registered
 * devices or querying an individual device by its identifier. Device
 * information is obtained directly from the persistence layer.
 *
 * <p>The controller provides read-only access to the device registry
 * maintained by the Digital Twin backend.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceRepository deviceRepository;

    /**
     * Creates a controller for accessing device information.
     *
     * @param deviceRepository repository used to retrieve device records
     */
    public DeviceController(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }
    /**
     * Retrieves all registered devices.
     *
     * @return a list containing all registered devices
     */
    @GetMapping
    public List<DeviceEntity> getAllDevices() {
        return deviceRepository.findAll();
    }
    /**
     * Retrieves a device by its unique identifier.
     *
     * @param deviceId the unique identifier of the device
     * @return the requested device if found; otherwise a 404 response
     */
    @GetMapping("/{deviceId}")
    public ResponseEntity<DeviceEntity> getDevice(@PathVariable String deviceId) {
        return deviceRepository.findById(deviceId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}