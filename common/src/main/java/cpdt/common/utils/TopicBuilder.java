package cpdt.common.utils;

import cpdt.common.dto.TelemetryPacket;

import java.util.Objects;

/**
 * Utility class for constructing MQTT topic strings from {@link TelemetryPacket} metadata.
 *
 * <p>Generated topics follow the format:
 * {@code cpdt/telemetry/{processArea}/{deviceType}/{deviceId}}.
 *
 * <p>This is a utility class and cannot be instantiated.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
public final class TopicBuilder {

    private TopicBuilder() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Builds the MQTT topic for the specified telemetry packet.
     *
     * @param packet the telemetry packet containing the topic metadata
     * @return the formatted MQTT topic string
     */
    public static String build(TelemetryPacket packet) {
        Objects.requireNonNull(packet, "Telemetry packet cannot be null");
        return String.format("cpdt/telemetry/%s/%s/%s", packet.processArea(), packet.deviceType(), packet.deviceId());
    }
}