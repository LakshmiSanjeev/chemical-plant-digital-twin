package cpdt.backend.exception;

/**
 * Indicates that an incoming telemetry packet failed validation
 * and cannot be processed.
 *
 * <p>This exception is thrown whenever a telemetry message contains
 * missing, malformed, or otherwise invalid data that prevents it
 * from being safely ingested by the backend.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
public class InvalidTelemetryException extends RuntimeException {
    /**
     * Creates a new invalid telemetry exception with the specified message.
     *
     * @param message the detail message describing the validation failure
     */
    public InvalidTelemetryException(String message) {
        super(message);
    }
    /**
     * Creates a new invalid telemetry exception with the specified
     * message and underlying cause.
     *
     * @param message the detail message describing the validation failure
     * @param cause the underlying cause of the exception
     */
    public InvalidTelemetryException(String message, Throwable cause) {
        super(message, cause);
    }
}