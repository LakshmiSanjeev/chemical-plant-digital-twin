package cpdt.backend;

import cpdt.backend.exception.InvalidTelemetryException;
import cpdt.backend.mqtt.TelemetryMessageHandler;
import cpdt.backend.services.TelemetryIngestionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TelemetryMessageHandlerTest {

    @Mock
    private TelemetryIngestionService telemetryIngestionService;

    @InjectMocks
    private TelemetryMessageHandler telemetryMessageHandler;

    private byte[] payload;
    private Message<byte[]> message;

    @BeforeEach
    void setUp() {
        payload = "sample telemetry".getBytes();
        message = MessageBuilder.withPayload(payload).build();
    }

    @Test
    void shouldDelegateTelemetryToIngestionService() {
        telemetryMessageHandler.handle(message);
        verify(telemetryIngestionService).ingest(payload);
    }

    @Test
    void shouldIgnoreInvalidTelemetryException() {
        doThrow(new InvalidTelemetryException("Invalid telemetry")).when(telemetryIngestionService).ingest(payload);
        assertDoesNotThrow(() -> telemetryMessageHandler.handle(message));
        verify(telemetryIngestionService).ingest(payload);
    }

    @Test
    void shouldHandleUnexpectedException() {
        doThrow(new RuntimeException("Unexpected failure")).when(telemetryIngestionService).ingest(payload);
        assertDoesNotThrow(() -> telemetryMessageHandler.handle(message));
        verify(telemetryIngestionService).ingest(payload);
    }
}