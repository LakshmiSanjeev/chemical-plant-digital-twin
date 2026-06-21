package cpdt.simulator.engine;

import cpdt.common.dto.TelemetryPacket;

public interface TelemetryPublisher {
    void publish(TelemetryPacket telemetryPacket);
}