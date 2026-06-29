package cpdt.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public final class TelemetrySerializer {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private TelemetrySerializer() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static byte[] toJson(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(object);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object", e);
        }
    }

    public static String toJsonString(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object", e);
        }
    }

    public static <T> T fromJson(byte[] payload, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(payload, clazz);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to deserialize object", e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to deserialize object", e);
        }
    }
}