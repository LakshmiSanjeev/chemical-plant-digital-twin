package cpdt.backend.mqtt;

import cpdt.backend.exception.InvalidTelemetryException;
import cpdt.backend.services.TelemetryIngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * Handles telemetry messages received from the MQTT broker.
 *
 * <p>This component receives telemetry payloads from the MQTT input
 * channel and forwards them to the telemetry ingestion service for
 * validation, persistence, and Digital Twin synchronization.
 *
 * <p>Malformed telemetry packets are rejected while unexpected errors
 * are logged without interrupting message processing.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TelemetryMessageHandler {

    private final TelemetryIngestionService telemetryIngestionService;

    /**
     * Processes an incoming MQTT telemetry message.
     *
     * <p>The message payload is extracted and forwarded to the telemetry
     * ingestion service. Validation failures and unexpected processing
     * errors are logged appropriately.
     *
     * @param message the MQTT message containing telemetry data
     */
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handle(Message<byte[]> message) {
        byte[] payload = message.getPayload();
        try {
            telemetryIngestionService.ingest(payload);
        }
        catch (InvalidTelemetryException e) {
            log.warn("Rejected malformed telemetry packet: {}", e.getMessage());
        }
        catch (Exception e) {
            log.error("Unexpected error while processing MQTT message", e);
        }
    }
}