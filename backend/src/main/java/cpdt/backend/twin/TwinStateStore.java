package cpdt.backend.twin;

import cpdt.common.dto.TelemetryPacket;
import cpdt.common.enums.ProcessArea;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Maintains the current Digital Twin state of all registered devices.
 *
 * <p>This component stores an in-memory representation of the latest
 * telemetry received for each device. It updates device state as new
 * telemetry arrives and provides query operations for retrieving the
 * current Digital Twin state of individual devices or process areas.
 *
 * <p>The state store serves as the backend's real-time Digital Twin
 * and is designed for concurrent access during telemetry processing.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
@Component
public class TwinStateStore {

    private final ConcurrentHashMap<String, TwinDeviceState> twinStates = new ConcurrentHashMap<>();

    /**
     * Updates the Digital Twin state using an incoming telemetry packet.
     *
     * @param packet the telemetry packet containing the latest device state
     */
    public void update(TelemetryPacket packet) {
        TwinDeviceState state = twinStates.computeIfAbsent(packet.deviceId(), TwinDeviceState::new);
        state.setLatestValue(packet.value());
        state.setLatestStatus(packet.status());
        state.setMeasurementType(packet.measurementType());
        state.setProcessArea(packet.processArea());
        state.setLastUpdated(Instant.ofEpochMilli(packet.timestamp()));
    }
    /**
     * Retrieves an immutable view of all stored Digital Twin states.
     *
     * @return a map containing the current state of all devices
     */
    public Map<String, TwinDeviceState> getTwinStates() {
        return Map.copyOf(twinStates);
    }
    /**
     * Retrieves the current state of all devices in the Digital Twin.
     *
     * @return a collection containing the current state of every device
     */
    public Collection<TwinDeviceState> getAllStates() {
        return List.copyOf(twinStates.values());
    }
    /**
     * Retrieves the current Digital Twin state of a specific device.
     *
     * @param deviceId the unique identifier of the device
     * @return an optional containing the current device state
     */
    public Optional<TwinDeviceState> getState(String deviceId) {
        return Optional.ofNullable(twinStates.get(deviceId));
    }
    /**
     * Retrieves the current Digital Twin state of devices within a process area.
     *
     * @param processArea the process area to query
     * @return a list containing the current state of matching devices
     */
    public List<TwinDeviceState> getStatesByProcessArea(ProcessArea processArea) {
        return twinStates.values().stream().filter(state -> state.getProcessArea() == processArea).toList();
    }
}