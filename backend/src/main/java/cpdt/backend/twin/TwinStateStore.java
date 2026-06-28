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

@Component
public class TwinStateStore {

    private final ConcurrentHashMap<String, TwinDeviceState> twinStates =
            new ConcurrentHashMap<>();

    public void update(TelemetryPacket packet) {

        TwinDeviceState state = twinStates.computeIfAbsent(
                packet.deviceId(),
                TwinDeviceState::new
        );

        state.setLatestValue(packet.value());
        state.setLatestStatus(packet.status());
        state.setMeasurementType(packet.measurementType());
        state.setProcessArea(packet.processArea());
        state.setLastUpdated(Instant.ofEpochMilli(packet.timestamp()));
    }

    public Map<String, TwinDeviceState> getTwinStates() {
        return Map.copyOf(twinStates);
    }

    public Collection<TwinDeviceState> getAllStates() {
        return List.copyOf(twinStates.values());
    }

    public Optional<TwinDeviceState> getState(String deviceId) {
        return Optional.ofNullable(twinStates.get(deviceId));
    }

    public List<TwinDeviceState> getStatesByProcessArea(ProcessArea processArea) {
        return twinStates.values()
                .stream()
                .filter(state -> state.getProcessArea() == processArea)
                .toList();
    }
}