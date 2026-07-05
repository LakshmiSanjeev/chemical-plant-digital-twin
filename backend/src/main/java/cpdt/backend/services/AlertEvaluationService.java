package cpdt.backend.services;

import cpdt.backend.entities.AlarmThresholdEntity;
import cpdt.backend.repositories.AlarmThresholdRepository;
import cpdt.common.dto.AlertMessage;
import cpdt.common.dto.TelemetryPacket;
import cpdt.common.enums.AlarmState;
import cpdt.common.enums.AlertSeverity;
import cpdt.common.enums.DeviceStatus;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Evaluates telemetry measurements to determine alarm conditions.
 *
 * <p>This service compares incoming telemetry values against configured
 * alarm thresholds to determine the appropriate alarm severity. It
 * manages alarm state transitions, updates device status, and generates
 * alert messages whenever a significant alarm event occurs.
 *
 * <p>The service forms the core of the backend's alarm evaluation
 * pipeline by converting raw telemetry into actionable alarms.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class AlertEvaluationService {

    private final AlarmThresholdRepository alarmThresholdRepository;
    private final DeviceStatusService deviceStatusService;
    private final AlarmStateService alarmStateService;

    /**
     * Evaluates a telemetry packet for alarm conditions.
     *
     * @param telemetryPacket the telemetry measurement to evaluate
     * @return an optional alert generated from the evaluation
     */
    public Optional<AlertMessage> evaluate(TelemetryPacket telemetryPacket) {
        Optional<AlarmThresholdEntity> thresholdOptional =
                alarmThresholdRepository.findByProcessAreaAndMeasurementType(telemetryPacket.processArea(), telemetryPacket.measurementType());

        if (thresholdOptional.isEmpty()) {
            return Optional.empty();
        }

        AlarmThresholdEntity threshold = thresholdOptional.get();
        if (!threshold.isEnabled()) {
            return Optional.empty();
        }

        AlertSeverity newSeverity = determineSeverity(telemetryPacket.value(), threshold);
        if (newSeverity == null) {
            alarmStateService.clearAlarm(telemetryPacket.deviceId());
            deviceStatusService.updateStatus(telemetryPacket.deviceId(), DeviceStatus.ONLINE, "All alarm conditions cleared.");
            return Optional.empty();
        }

        AlarmState alarmState = alarmStateService.updateAlarmState(telemetryPacket.deviceId(), newSeverity);
        deviceStatusService.updateStatus(telemetryPacket.deviceId(), mapStatus(newSeverity), buildMessage(telemetryPacket, newSeverity));
        if (alarmState == AlarmState.NO_CHANGE) {
            return Optional.empty();
        }

        AlertMessage alert = new AlertMessage(UUID.randomUUID().toString(), telemetryPacket.deviceId(), newSeverity, buildMessage(telemetryPacket, newSeverity), System.currentTimeMillis());
        return Optional.of(alert);
    }
    /**
     * Determines the alarm severity associated with a measurement value.
     *
     * @param value the measured value
     * @param threshold the configured alarm thresholds
     * @return the corresponding alert severity, or {@code null} if no alarm exists
     */
    private AlertSeverity determineSeverity(double value, AlarmThresholdEntity threshold) {
        if (value <= threshold.getCriticalLowThreshold()) {
            return AlertSeverity.CRITICAL_LOW;
        }

        if (value <= threshold.getWarningLowThreshold()) {
            return AlertSeverity.WARNING_LOW;
        }

        if (value >= threshold.getCriticalHighThreshold()) {
            return AlertSeverity.CRITICAL_HIGH;
        }

        if (value >= threshold.getWarningHighThreshold()) {
            return AlertSeverity.WARNING_HIGH;
        }

        return null;
    }
    /**
     * Builds a descriptive alarm message for an evaluated telemetry value.
     *
     * @param telemetryPacket the evaluated telemetry packet
     * @param severity the detected alarm severity
     * @return the formatted alarm message
     */
    private String buildMessage(TelemetryPacket telemetryPacket, AlertSeverity severity) {
        return String.format("%s: %s is %.2f %s in %s.",
                severity,
                telemetryPacket.measurementType().getDisplayName(),
                telemetryPacket.value(),
                telemetryPacket.measurementType().getUnit(),
                telemetryPacket.processArea()
        );
    }
    /**
     * Maps an alert severity to the corresponding device status.
     *
     * @param severity the evaluated alert severity
     * @return the corresponding device status
     */
    private DeviceStatus mapStatus(AlertSeverity severity) {
        return switch (severity) {
            case WARNING_LOW, WARNING_HIGH -> DeviceStatus.WARNING;
            case CRITICAL_LOW, CRITICAL_HIGH -> DeviceStatus.CRITICAL;
            case INFO -> DeviceStatus.ONLINE;
        };
    }
}