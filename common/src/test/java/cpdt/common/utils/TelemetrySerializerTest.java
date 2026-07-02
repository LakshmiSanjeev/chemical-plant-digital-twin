package cpdt.common.utils;

import cpdt.common.dto.TelemetryPacket;
import cpdt.common.enums.DeviceStatus;
import cpdt.common.enums.DeviceType;
import cpdt.common.enums.MeasurementType;
import cpdt.common.enums.ProcessArea;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TelemetrySerializerTest {

    private TelemetryPacket createPacket() {
        return new TelemetryPacket(
                "TEMP-001",
                "Temperature Sensor",
                DeviceType.TEMPERATURE_SENSOR,
                DeviceStatus.ONLINE,
                "LOC-001",
                "Reactor",
                ProcessArea.REACTOR_SECTION,
                123456789L,
                MeasurementType.TEMPERATURE,
                42.5
        );
    }

    @Test
    void shouldSerializeTelemetryPacketToJsonString() {
        TelemetryPacket packet = createPacket();
        String json = TelemetrySerializer.toJsonString(packet);
        assertNotNull(json);
        assertTrue(json.contains("\"deviceId\":\"TEMP-001\""));
        assertTrue(json.contains("\"deviceName\":\"Temperature Sensor\""));
        assertTrue(json.contains("\"processArea\":\"REACTOR_SECTION\""));
        assertTrue(json.contains("\"measurementType\":\"TEMPERATURE\""));
    }

    @Test
    void shouldSerializeTelemetryPacketToByteArray() {
        TelemetryPacket packet = createPacket();
        byte[] jsonBytes = TelemetrySerializer.toJson(packet);
        assertNotNull(jsonBytes);
        assertTrue(jsonBytes.length > 0);
    }

    @Test
    void shouldDeserializeJsonStringToTelemetryPacket() {
        TelemetryPacket original = createPacket();
        String json = TelemetrySerializer.toJsonString(original);
        TelemetryPacket deserialized = TelemetrySerializer.fromJson(json, TelemetryPacket.class);
        assertEquals(original, deserialized);
    }

    @Test
    void shouldDeserializeByteArrayToTelemetryPacket() {
        TelemetryPacket original = createPacket();
        byte[] json = TelemetrySerializer.toJson(original);
        TelemetryPacket deserialized = TelemetrySerializer.fromJson(json, TelemetryPacket.class);
        assertEquals(original, deserialized);
    }

    @Test
    void shouldPreserveAllFieldsAfterRoundTripSerialization() {
        TelemetryPacket original = createPacket();
        String json = TelemetrySerializer.toJsonString(original);
        TelemetryPacket restored = TelemetrySerializer.fromJson(json, TelemetryPacket.class);
        assertAll(
                () -> assertEquals(original.deviceId(), restored.deviceId()),
                () -> assertEquals(original.deviceName(), restored.deviceName()),
                () -> assertEquals(original.deviceType(), restored.deviceType()),
                () -> assertEquals(original.status(), restored.status()),
                () -> assertEquals(original.locationId(), restored.locationId()),
                () -> assertEquals(original.locationName(), restored.locationName()),
                () -> assertEquals(original.processArea(), restored.processArea()),
                () -> assertEquals(original.timestamp(), restored.timestamp()),
                () -> assertEquals(original.measurementType(), restored.measurementType()),
                () -> assertEquals(original.value(), restored.value())
        );
    }

    @Test
    void shouldThrowRuntimeExceptionForInvalidJsonString() {
        String invalidJson = "{ invalid json }";
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> TelemetrySerializer.fromJson(invalidJson, TelemetryPacket.class));
        assertTrue(exception.getMessage().contains("Failed to deserialize"));
    }

    @Test
    void shouldThrowRuntimeExceptionForInvalidJsonBytes() {
        byte[] invalidBytes = "not-json".getBytes();
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> TelemetrySerializer.fromJson(invalidBytes, TelemetryPacket.class));
        assertTrue(exception.getMessage().contains("Failed to deserialize"));
    }

    @Test
    void shouldSerializeNullObjectAsJsonNull() {
        String json = TelemetrySerializer.toJsonString(null);
        assertEquals("null", json);
    }

    @Test
    void shouldDeserializeJsonNullToNullObject() {
        TelemetryPacket packet = TelemetrySerializer.fromJson("null", TelemetryPacket.class);
        assertNull(packet);
    }
}