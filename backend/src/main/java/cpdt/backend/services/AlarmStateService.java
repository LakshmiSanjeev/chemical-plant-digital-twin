package cpdt.backend.services;

import cpdt.common.enums.AlarmState;
import cpdt.common.enums.AlertSeverity;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class AlarmStateService {

    private final ConcurrentMap<String, AlertSeverity> activeAlarms =
            new ConcurrentHashMap<>();

    public AlarmState updateAlarmState(
            String deviceId,
            AlertSeverity newSeverity) {

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

        // Same alarm level (e.g. WARNING_LOW -> WARNING_HIGH).
        // Update the stored severity but don't treat it as a state transition.
        activeAlarms.put(deviceId, newSeverity);
        return AlarmState.NO_CHANGE;
    }

    public AlarmState clearAlarm(String deviceId) {

        return activeAlarms.remove(deviceId) != null
                ? AlarmState.CLEARED
                : AlarmState.NO_CHANGE;
    }

    public boolean hasActiveAlarm(String deviceId) {
        return activeAlarms.containsKey(deviceId);
    }

    private int severityLevel(AlertSeverity severity) {

        return switch (severity) {
            case INFO -> 0;
            case WARNING_LOW, WARNING_HIGH -> 1;
            case CRITICAL_LOW, CRITICAL_HIGH -> 2;
        };
    }

    public AlertSeverity getCurrentSeverity(String deviceId) {
        return activeAlarms.get(deviceId);
    }
}