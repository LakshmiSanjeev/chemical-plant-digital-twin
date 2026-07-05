package cpdt.backend.services;

import cpdt.common.enums.AlarmState;
import cpdt.common.enums.AlertSeverity;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Tracks the active alarm state of registered devices.
 *
 * <p>This service maintains the current alarm severity for each device
 * and determines whether an incoming alarm represents a new alarm,
 * escalation, de-escalation, clearance, or no state change. It enables
 * the backend to suppress duplicate alarms while detecting meaningful
 * alarm transitions.
 *
 * <p>The service stores alarm state in memory and supports thread-safe
 * access during concurrent telemetry processing.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
@Service
public class AlarmStateService {

    private final ConcurrentMap<String, AlertSeverity> activeAlarms = new ConcurrentHashMap<>();

    /**
     * Updates the alarm state of a device based on its latest severity.
     *
     * @param deviceId the unique identifier of the device
     * @param newSeverity the newly evaluated alarm severity
     * @return the resulting alarm state transition
     */
    public AlarmState updateAlarmState(String deviceId, AlertSeverity newSeverity) {
        AlertSeverity currentSeverity = activeAlarms.get(deviceId);
        if (currentSeverity == null) {
            activeAlarms.put(deviceId, newSeverity);
            return AlarmState.NEW_ALARM;
        }
        int currentLevel = severityLevel(currentSeverity);
        int newLevel = severityLevel(newSeverity);
        if (newLevel > currentLevel) {
            activeAlarms.put(deviceId, newSeverity);
            return AlarmState.ESCALATED;
        }
        if (newLevel < currentLevel) {
            activeAlarms.put(deviceId, newSeverity);
            return AlarmState.DEESCALATED;
        }
        activeAlarms.put(deviceId, newSeverity);
        return AlarmState.NO_CHANGE;
    }
    /**
     * Clears the active alarm associated with a device.
     *
     * @param deviceId the unique identifier of the device
     * @return the resulting alarm state transition
     */
    public AlarmState clearAlarm(String deviceId) {
        return activeAlarms.remove(deviceId) != null ? AlarmState.CLEARED : AlarmState.NO_CHANGE;
    }
    /**
     * Checks if a device currently has an active alarm.
     *
     * @param deviceId the unique identifier of the device
     * @return {@code true} if an active alarm exists; otherwise {@code false}
     */
    public boolean hasActiveAlarm(String deviceId) {
        return activeAlarms.containsKey(deviceId);
    }
    /**
     * Converts an alert severity into its relative priority level.
     *
     * @param severity the alert severity
     * @return the numeric severity level
     */
    private int severityLevel(AlertSeverity severity) {
        return switch (severity) {
            case INFO -> 0;
            case WARNING_LOW, WARNING_HIGH -> 1;
            case CRITICAL_LOW, CRITICAL_HIGH -> 2;
        };
    }
    /**
     * Retrieves the current alarm severity of a device.
     *
     * @param deviceId the unique identifier of the device
     * @return the current alert severity, or {@code null} if no alarm exists
     */
    public AlertSeverity getCurrentSeverity(String deviceId) {
        return activeAlarms.get(deviceId);
    }
}