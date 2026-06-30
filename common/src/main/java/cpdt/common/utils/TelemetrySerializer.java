package cpdt.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cpdt.common.dto.TelemetryPacket;
import cpdt.common.enums.DeviceType;

import java.io.IOException;

/**
 * Utility class responsible for serializing {@link TelemetryPacket} objects
 * into JSON strings or byte arrays, and deserializing JSON strings or byte
 * arrays back into {@link TelemetryPacket} objects.
 *
 * <p>This class provides a centralized interface for JSON conversion
 * operations used by both the simulator and backend modules, ensuring a
 * consistent serialization format throughout the application.
 *
 * <p>The utility is thread-safe because {@link ObjectMapper} is designed
 * for concurrent use after configuration.
 *
 * <p>This is a utility class and cannot be instantiated.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
public final class TelemetrySerializer {
    /**
     * Shared Jackson object mapper used for all serialization and
     * deserialization operations.
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private TelemetrySerializer() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Serializes the specified object into its JSON byte array representation.
     *
     * <p>This method converts any Jackson-serializable object into UTF-8
     * encoded JSON bytes suitable for transmission over network protocols
     * such as MQTT or for binary storage.
     *
     * @param object the object to serialize
     * @return the serialized JSON as a byte array
     * @throws RuntimeException if the object cannot be serialized
     */
    public static byte[] toJson(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(object);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object", e);
        }
    }
    /**
     * Serializes the specified object into its JSON string representation.
     *
     * <p>The resulting JSON string may be used for logging, debugging,
     * storage, or transmission to components expecting textual JSON.
     *
     * @param object the object to serialize
     * @return the serialized JSON string
     * @throws RuntimeException if the object cannot be serialized
     */
    public static String toJsonString(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object", e);
        }
    }
    /**
     * Deserializes a JSON byte array into an instance of the specified class.
     *
     * <p>The supplied byte array must contain valid JSON representing an
     * object compatible with the requested target class.
     *
     * @param payload the JSON byte array to deserialize
     * @param clazz the target class of the resulting object
     * @param <T> the type of object to be returned
     * @return the deserialized object
     * @throws RuntimeException if the JSON cannot be deserialized into the requested type
     */
    public static <T> T fromJson(byte[] payload, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(payload, clazz);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to deserialize object", e);
        }
    }
    /**
     * Deserializes a JSON string into an instance of the specified class.
     *
     * <p>The supplied JSON string must represent an object compatible with
     * the requested target class.
     *
     * @param json the JSON string to deserialize
     * @param clazz the target class of the resulting object
     * @param <T> the type of object to be returned
     * @return the deserialized object
     * @throws RuntimeException if the JSON cannot be deserialized into the requested type
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to deserialize object", e);
        }
    }
}