package cpdt.backend.controllers;

import cpdt.backend.twin.TwinDeviceState;
import cpdt.backend.twin.TwinStateStore;
import cpdt.common.enums.ProcessArea;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * Exposes REST endpoints for accessing the Digital Twin state.
 *
 * <p>This controller provides read access to the current Digital Twin
 * representation maintained by the backend. Clients can retrieve the
 * complete plant state, query individual devices, or obtain the state
 * of devices belonging to a specific process area.
 *
 * <p>The controller delegates all state retrieval operations to the
 * Digital Twin state store.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
@RestController
@RequestMapping("/api/twin")
@RequiredArgsConstructor
public class TwinController {

    private final TwinStateStore twinStateStore;

    /**
     * Retrieves the current state of all devices maintained by the Digital Twin.
     *
     * @return a collection containing the current state of every device
     */
    @GetMapping
    public Collection<TwinDeviceState> getTwin() {
        return twinStateStore.getAllStates();
    }
    /**
     * Retrieves the current Digital Twin state of a specific device.
     *
     * @param deviceId the unique identifier of the device
     * @return the current device state if found; otherwise a 404 response
     */
    @GetMapping("/{deviceId}")
    public ResponseEntity<TwinDeviceState> getDeviceState(@PathVariable("deviceId") String deviceId){
        return twinStateStore.getState(deviceId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    /**
     * Retrieves the current Digital Twin state for all devices within a
     * specified process area.
     *
     * @param processArea the process area to query
     * @return a list containing the current state of all matching devices
     */
    @GetMapping("/area/{processArea}")
    public List<TwinDeviceState> getAreaState(@PathVariable ProcessArea processArea) {
        return twinStateStore.getStatesByProcessArea(processArea);
    }
}