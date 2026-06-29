package cpdt.backend.mqtt;

import cpdt.backend.exception.InvalidTelemetryException;
import cpdt.backend.services.TelemetryIngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelemetryMessageHandler {

    private final TelemetryIngestionService telemetryIngestionService;

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handle(Message<byte[]> message) {

        byte[] payload = message.getPayload();

        try {
            telemetryIngestionService.ingest(payload);
        } catch (InvalidTelemetryException e) {
            log.warn("Rejected malformed telemetry packet: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while processing MQTT message", e);
        }
    }
}