package cpdt.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;

/**
 * Provides centralized exception handling for REST controllers.
 *
 * <p>This class intercepts exceptions thrown during request processing
 * and converts them into standardized HTTP error responses. It ensures
 * consistent error reporting across the backend while preventing
 * unhandled exceptions from being exposed to API clients.
 *
 * <p>The handler processes both application-specific exceptions and
 * common Spring framework exceptions.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles invalid telemetry exceptions.
     *
     * @param ex the exception describing the validation failure
     * @return a response containing the corresponding error details
     */
    @ExceptionHandler(InvalidTelemetryException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTelemetry(InvalidTelemetryException ex) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    /**
     * Handles unexpected exceptions that occur during request processing.
     *
     * @param ex the exception that was thrown
     * @return a response containing the corresponding error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                ex.getMessage(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    /**
     * Handles invalid request parameter type conversions.
     *
     * @param ex the exception describing the invalid request parameter
     * @return a response containing the corresponding error details
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                Instant.now()
        );
        return ResponseEntity.badRequest().body(response);
    }
    /**
     * Handles requests with missing required parameters.
     *
     * @param ex the exception describing the missing request parameter
     * @return a response containing the corresponding error details
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestParameter(MissingServletRequestParameterException ex) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                Instant.now()
        );
        return ResponseEntity.badRequest().body(response);
    }
}