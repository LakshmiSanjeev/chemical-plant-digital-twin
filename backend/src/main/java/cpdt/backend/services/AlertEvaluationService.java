package cpdt.backend.services;

import cpdt.backend.entities.AlarmThresholdEntity;
import cpdt.backend.repositories.AlarmThresholdRepository;
import cpdt.common.dto.AlertMessage;
import cpdt.common.dto.TelemetryPacket;
import cpdt.common.enums.AlarmState;
import cpdt.common.enums.AlertSeverity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlertEvaluationService {

    private final AlarmThresholdRepository alarmThresholdRepository;
    private final AlarmStateService alarmStateService;

    public Optional<AlertMessage> evaluate(TelemetryPacket telemetryPacket) {

        System.out.println("[ALERT] Entered evaluate()");

        Optional<AlarmThresholdEntity> thresholdOptional =
                alarmThresholdRepository.findByProcessAreaAndMeasurementType(
                        telemetryPacket.processArea(),
                        telemetryPacket.measurementType()
                );

        System.out.println("Threshold present = " + thresholdOptional.isPresent());

        if (thresholdOptional.isEmpty()) {
            return Optional.empty();
        }

        AlarmThresholdEntity threshold = thresholdOptional.get();

        if (!threshold.isEnabled()) {
            return Optional.empty();
        }

        AlertSeverity newSeverity =
                determineSeverity(telemetryPacket.value(), threshold);

        System.out.println("Severity = " + newSeverity);

        if (newSeverity == null) {

            alarmStateService.clearAlarm(telemetryPacket.deviceId());

            return Optional.empty();
        }

        AlarmState alarmState =
                alarmStateService.updateAlarmState(
                        telemetryPacket.deviceId(),
                        newSeverity
                );

        if (alarmState == AlarmState.NO_CHANGE) {
            return Optional.empty();
        }

        AlertMessage alert = new AlertMessage(
                UUID.randomUUID().toString(),
                telemetryPacket.deviceId(),
                newSeverity,
                buildMessage(telemetryPacket, newSeverity),
                System.currentTimeMillis()
        );

        System.out.println("Returning AlertMessage");

        return Optional.of(alert);
    }

    private AlertSeverity determineSeverity(
            double value,
            AlarmThresholdEntity threshold) {

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

    private String buildMessage(
            TelemetryPacket telemetryPacket,
            AlertSeverity severity) {

        System.out.println("========== ALERT EVALUATION ==========");
        System.out.println("Device: " + telemetryPacket.deviceId());
        System.out.println("Measurement: " + telemetryPacket.measurementType());
        System.out.println("Area: " + telemetryPacket.processArea());
        System.out.println("Value: " + telemetryPacket.value());

        return String.format(
                "%s: %s is %.2f %s in %s.",
                severity,
                telemetryPacket.measurementType().getDisplayName(),
                telemetryPacket.value(),
                telemetryPacket.measurementType().getUnit(),
                telemetryPacket.processArea()
        );
    }
}