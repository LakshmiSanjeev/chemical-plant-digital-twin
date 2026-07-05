package cpdt.backend.services;

import cpdt.common.enums.AlarmState;
import cpdt.common.enums.AlertSeverity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlarmStateServiceTest {

    private AlarmStateService alarmStateService;

    @BeforeEach
    void setUp() {
        alarmStateService = new AlarmStateService();
    }

    @Test
    void shouldCreateNewAlarmWhenDeviceHasNoExistingAlarm() {
        AlarmState state = alarmStateService.updateAlarmState("DEVICE-001", AlertSeverity.WARNING_HIGH);
        assertEquals(AlarmState.NEW_ALARM, state);
        assertTrue(alarmStateService.hasActiveAlarm("DEVICE-001"));
        assertEquals(AlertSeverity.WARNING_HIGH, alarmStateService.getCurrentSeverity("DEVICE-001"));
    }

    @Test
    void shouldEscalateAlarmWhenSeverityIncreases() {
        alarmStateService.updateAlarmState("DEVICE-001", AlertSeverity.WARNING_HIGH);
        AlarmState state = alarmStateService.updateAlarmState("DEVICE-001", AlertSeverity.CRITICAL_HIGH);
        assertEquals(AlarmState.ESCALATED, state);
        assertEquals(AlertSeverity.CRITICAL_HIGH, alarmStateService.getCurrentSeverity("DEVICE-001"));
    }

    @Test
    void shouldDeEscalateAlarmWhenSeverityDecreases() {
        alarmStateService.updateAlarmState("DEVICE-001", AlertSeverity.CRITICAL_HIGH);
        AlarmState state = alarmStateService.updateAlarmState("DEVICE-001", AlertSeverity.WARNING_HIGH);
        assertEquals(AlarmState.DEESCALATED, state);
        assertEquals(AlertSeverity.WARNING_HIGH, alarmStateService.getCurrentSeverity("DEVICE-001"));
    }

    @Test
    void shouldReturnNoChangeWhenSeverityRemainsSame() {
        alarmStateService.updateAlarmState("DEVICE-001", AlertSeverity.WARNING_LOW);
        AlarmState state = alarmStateService.updateAlarmState("DEVICE-001", AlertSeverity.WARNING_LOW);
        assertEquals(AlarmState.NO_CHANGE, state);
        assertEquals(AlertSeverity.WARNING_LOW, alarmStateService.getCurrentSeverity("DEVICE-001"));
    }

    @Test
    void shouldClearExistingAlarm() {
        alarmStateService.updateAlarmState("DEVICE-001", AlertSeverity.WARNING_HIGH);
        AlarmState state = alarmStateService.clearAlarm("DEVICE-001");
        assertEquals(AlarmState.CLEARED, state);
        assertFalse(alarmStateService.hasActiveAlarm("DEVICE-001"));
        assertNull(alarmStateService.getCurrentSeverity("DEVICE-001"));
    }

    @Test
    void shouldReturnNoChangeWhenClearingNonExistingAlarm() {
        AlarmState state = alarmStateService.clearAlarm("UNKNOWN");
        assertEquals(AlarmState.NO_CHANGE, state);
    }

    @Test
    void shouldReturnFalseWhenNoActiveAlarmExists() {
        assertFalse(alarmStateService.hasActiveAlarm("DEVICE-001"));
    }

    @Test
    void shouldReturnNullSeverityForUnknownDevice() {
        assertNull(alarmStateService.getCurrentSeverity("UNKNOWN"));
    }
}