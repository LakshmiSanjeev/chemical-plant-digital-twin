package cpdt.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cpdt.common.dto.TelemetryPacket;

import java.io.IOException;

public final class TelemetrySerializer {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private TelemetrySerializer() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static byte[] toJson(TelemetryPacket packet) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(packet);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize telemetry packet", e);
        }
    }

    public static String toJsonString(TelemetryPacket packet) {
        try {
            return OBJECT_MAPPER.writeValueAsString(packet);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize telemetry packet", e);
        }
    }

    public static TelemetryPacket fromJson(byte[] payload) {
        try {
            return OBJECT_MAPPER.readValue(payload, TelemetryPacket.class);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to deserialize telemetry packet", e);
        }
    }

    public static TelemetryPacket fromJson(String json) {
        try {
            return OBJECT_MAPPER.readValue(json, TelemetryPacket.class);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to deserialize telemetry packet", e);
        }
    }
}