package cpdt.simulator.engine;

import cpdt.common.dto.TelemetryPacket;

public class ConsoleTelemetryPublisher implements TelemetryPublisher {
    @Override
    public void publish(TelemetryPacket packet) {
        System.out.printf(
                "[%tT] %-10s %-20s %-20s %-22s %-15s %10.3f%n",
                packet.timestamp(),
                packet.deviceId(),
                packet.deviceType(),
                packet.locationName(),
                packet.processArea(),
                packet.measurementType(),
                packet.value()
        );
    }
}