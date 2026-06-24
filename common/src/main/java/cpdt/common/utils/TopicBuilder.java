package cpdt.common.utils;

import cpdt.common.dto.TelemetryPacket;

public final class TopicBuilder {

    private TopicBuilder() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String build(TelemetryPacket packet) {
        return String.format("cpdt/telemetry/%s/%s/%s", packet.processArea(), packet.deviceType(), packet.deviceId());
    }
}