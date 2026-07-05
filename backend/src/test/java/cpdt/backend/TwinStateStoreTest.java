package cpdt.backend;

import cpdt.backend.twin.TwinDeviceState;
import cpdt.backend.twin.TwinStateStore;
import cpdt.common.dto.TelemetryPacket;
import cpdt.common.enums.DeviceStatus;
import cpdt.common.enums.DeviceType;
import cpdt.common.enums.MeasurementType;
import cpdt.common.enums.ProcessArea;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TwinStateStoreTest {

    private TwinStateStore twinStateStore;

    @BeforeEach
    void setUp() {
        twinStateStore = new TwinStateStore();
    }

    private TelemetryPacket createPacket(String deviceId, ProcessArea processArea, double value, DeviceStatus status) {
        return new TelemetryPacket(
                deviceId,
                "Temperature Sensor",
                DeviceType.TEMPERATURE_SENSOR,
                status,
                "LOC-001",
                "Reactor",
                processArea,
                System.currentTimeMillis(),
                MeasurementType.TEMPERATURE,
                value
        );
    }

    @Test
    void shouldCreateNewTwinState() {
        TelemetryPacket packet = createPacket("DEVICE-001", ProcessArea.REACTOR_SECTION, 65.5, DeviceStatus.ONLINE);
        twinStateStore.update(packet);
        Optional<TwinDeviceState> result = twinStateStore.getState("DEVICE-001");
        assertTrue(result.isPresent());
        TwinDeviceState state = result.get();
        assertEquals("DEVICE-001", state.getDeviceId());
        assertEquals(65.5, state.getLatestValue());
        assertEquals(DeviceStatus.ONLINE, state.getLatestStatus());
        assertEquals(MeasurementType.TEMPERATURE, state.getMeasurementType());
        assertEquals(ProcessArea.REACTOR_SECTION, state.getProcessArea());
        assertEquals(Instant.ofEpochMilli(packet.timestamp()), state.getLastUpdated());
    }

    @Test
    void shouldUpdateExistingTwinState() {
        twinStateStore.update(createPacket("DEVICE-001", ProcessArea.REACTOR_SECTION, 40.0, DeviceStatus.ONLINE));
        TelemetryPacket updated = createPacket("DEVICE-001", ProcessArea.REACTOR_SECTION, 95.5, DeviceStatus.CRITICAL);
        twinStateStore.update(updated);
        TwinDeviceState state = twinStateStore.getState("DEVICE-001").orElseThrow();
        assertEquals(95.5, state.getLatestValue());
        assertEquals(DeviceStatus.CRITICAL, state.getLatestStatus());
        assertEquals(Instant.ofEpochMilli(updated.timestamp()), state.getLastUpdated());
    }

    @Test
    void shouldReturnAllStates() {
        twinStateStore.update(createPacket("DEVICE-001", ProcessArea.REACTOR_SECTION, 10, DeviceStatus.ONLINE));
        twinStateStore.update(createPacket("DEVICE-002", ProcessArea.STORAGE_SECTION, 20, DeviceStatus.WARNING));
        Collection<TwinDeviceState> states = twinStateStore.getAllStates();
        assertEquals(2, states.size());
    }

    @Test
    void shouldReturnTwinStatesMap() {
        twinStateStore.update(createPacket("DEVICE-001", ProcessArea.REACTOR_SECTION, 30, DeviceStatus.ONLINE));
        Map<String, TwinDeviceState> states = twinStateStore.getTwinStates();
        assertEquals(1, states.size());
        assertTrue(states.containsKey("DEVICE-001"));
    }

    @Test
    void shouldReturnStateByDeviceId() {
        twinStateStore.update(createPacket("DEVICE-001", ProcessArea.REACTOR_SECTION, 50, DeviceStatus.WARNING));
        Optional<TwinDeviceState> state = twinStateStore.getState("DEVICE-001");
        assertTrue(state.isPresent());
        assertEquals("DEVICE-001", state.get().getDeviceId());
    }

    @Test
    void shouldReturnEmptyForUnknownDevice() {
        Optional<TwinDeviceState> state = twinStateStore.getState("UNKNOWN");
        assertTrue(state.isEmpty());
    }

    @Test
    void shouldReturnStatesByProcessArea() {
        twinStateStore.update(createPacket("DEVICE-001", ProcessArea.REACTOR_SECTION, 50, DeviceStatus.ONLINE));
        twinStateStore.update(createPacket("DEVICE-002", ProcessArea.REACTOR_SECTION, 60, DeviceStatus.WARNING));
        twinStateStore.update(createPacket("DEVICE-003", ProcessArea.STORAGE_SECTION, 70, DeviceStatus.ONLINE));
        List<TwinDeviceState> reactorStates = twinStateStore.getStatesByProcessArea(ProcessArea.REACTOR_SECTION);
        assertEquals(2, reactorStates.size());
        assertTrue(reactorStates.stream().allMatch(state -> state.getProcessArea() == ProcessArea.REACTOR_SECTION));
    }

    @Test
    void shouldReturnEmptyListWhenNoDeviceMatchesProcessArea() {
        twinStateStore.update(createPacket("DEVICE-001", ProcessArea.REACTOR_SECTION, 50, DeviceStatus.ONLINE));
        List<TwinDeviceState> states = twinStateStore.getStatesByProcessArea(ProcessArea.PIPELINE_SECTION);
        assertTrue(states.isEmpty());
    }
}