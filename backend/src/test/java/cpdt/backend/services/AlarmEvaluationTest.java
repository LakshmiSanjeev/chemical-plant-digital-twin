package cpdt.backend.services;

import cpdt.backend.entities.AlarmThresholdEntity;
import cpdt.backend.repositories.AlarmThresholdRepository;
import cpdt.common.dto.AlertMessage;
import cpdt.common.dto.TelemetryPacket;
import cpdt.common.enums.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlarmEvaluationTest {

    @Mock
    private AlarmThresholdRepository alarmThresholdRepository;

    @Mock
    private DeviceStatusService deviceStatusService;

    @Mock
    private AlarmStateService alarmStateService;

    @InjectMocks
    private AlertEvaluationService alertEvaluationService;

    private AlarmThresholdEntity threshold;
    private TelemetryPacket telemetry;

    @BeforeEach
    void setUp() {
        threshold = new AlarmThresholdEntity();
        threshold.setEnabled(true);
        threshold.setWarningLowThreshold(20.0);
        threshold.setCriticalLowThreshold(10.0);
        threshold.setWarningHighThreshold(80.0);
        threshold.setCriticalHighThreshold(90.0);
        telemetry = createTelemetryPacket(50.0);
    }

    @Test
    void shouldReturnEmptyWhenThresholdDoesNotExist() {
        when(alarmThresholdRepository.findByProcessAreaAndMeasurementType(any(), any())).thenReturn(Optional.empty());
        Optional<AlertMessage> result = alertEvaluationService.evaluate(telemetry);
        assertTrue(result.isEmpty());
        verifyNoInteractions(deviceStatusService);
        verifyNoInteractions(alarmStateService);
    }

    @Test
    void shouldReturnEmptyWhenThresholdIsDisabled() {
        threshold.setEnabled(false);
        when(alarmThresholdRepository.findByProcessAreaAndMeasurementType(any(), any())).thenReturn(Optional.of(threshold));
        assertTrue(alertEvaluationService.evaluate(telemetry).isEmpty());
        verifyNoInteractions(deviceStatusService);
        verifyNoInteractions(alarmStateService);
    }

    @Test
    void shouldClearAlarmWhenValueReturnsToNormal() {
        when(alarmThresholdRepository.findByProcessAreaAndMeasurementType(telemetry.processArea(), telemetry.measurementType())).thenReturn(Optional.of(threshold));
        when(alarmStateService.clearAlarm("DEVICE-001")).thenReturn(AlarmState.CLEARED);
        Optional<AlertMessage> result = alertEvaluationService.evaluate(telemetry);
        assertTrue(result.isEmpty());
        verify(alarmStateService).clearAlarm("DEVICE-001");
        verify(deviceStatusService).updateStatus(eq("DEVICE-001"), eq(DeviceStatus.ONLINE), eq("All alarm conditions cleared."));
        verify(alarmThresholdRepository).findByProcessAreaAndMeasurementType(telemetry.processArea(), telemetry.measurementType());
        verifyNoMoreInteractions(deviceStatusService, alarmStateService);
    }

    @Test
    void shouldGenerateWarningHighAlert() {
        telemetry = createTelemetryPacket(85.0);
        when(alarmThresholdRepository.findByProcessAreaAndMeasurementType(telemetry.processArea(), telemetry.measurementType())).thenReturn(Optional.of(threshold));
        when(alarmStateService.updateAlarmState("DEVICE-001", AlertSeverity.WARNING_HIGH)).thenReturn(AlarmState.NEW_ALARM);
        Optional<AlertMessage> result = alertEvaluationService.evaluate(telemetry);
        assertTrue(result.isPresent());
        AlertMessage alert = result.get();
        assertEquals(AlertSeverity.WARNING_HIGH, alert.severity());
        assertEquals("DEVICE-001", alert.deviceId());
        verify(deviceStatusService).updateStatus(eq("DEVICE-001"), eq(DeviceStatus.WARNING), contains("WARNING_HIGH"));
    }

    @Test
    void shouldGenerateWarningLowAlert() {
        telemetry = createTelemetryPacket(15.0);
        when(alarmThresholdRepository.findByProcessAreaAndMeasurementType(telemetry.processArea(), telemetry.measurementType())).thenReturn(Optional.of(threshold));
        when(alarmStateService.updateAlarmState("DEVICE-001", AlertSeverity.WARNING_LOW)).thenReturn(AlarmState.NEW_ALARM);
        Optional<AlertMessage> result = alertEvaluationService.evaluate(telemetry);
        assertTrue(result.isPresent());
        AlertMessage alert = result.get();
        assertEquals(AlertSeverity.WARNING_LOW, alert.severity());
        assertEquals("DEVICE-001", alert.deviceId());
        verify(deviceStatusService).updateStatus(eq("DEVICE-001"), eq(DeviceStatus.WARNING), contains("WARNING_LOW"));
    }

    @Test
    void shouldGenerateCriticalHighAlert() {
        telemetry = createTelemetryPacket(95.0);
        when(alarmThresholdRepository.findByProcessAreaAndMeasurementType(telemetry.processArea(), telemetry.measurementType())).thenReturn(Optional.of(threshold));
        when(alarmStateService.updateAlarmState("DEVICE-001", AlertSeverity.CRITICAL_HIGH)).thenReturn(AlarmState.NEW_ALARM);
        Optional<AlertMessage> result = alertEvaluationService.evaluate(telemetry);
        assertTrue(result.isPresent());
        AlertMessage alert = result.get();
        assertEquals(AlertSeverity.CRITICAL_HIGH, alert.severity());
        assertEquals("DEVICE-001", alert.deviceId());
        verify(deviceStatusService).updateStatus(eq("DEVICE-001"), eq(DeviceStatus.CRITICAL), contains("CRITICAL_HIGH"));
    }

    @Test
    void shouldGenerateCriticalLowAlert() {
        telemetry = createTelemetryPacket(5.0);
        when(alarmThresholdRepository.findByProcessAreaAndMeasurementType(telemetry.processArea(), telemetry.measurementType())).thenReturn(Optional.of(threshold));
        when(alarmStateService.updateAlarmState("DEVICE-001", AlertSeverity.CRITICAL_LOW)).thenReturn(AlarmState.NEW_ALARM);
        Optional<AlertMessage> result = alertEvaluationService.evaluate(telemetry);
        assertTrue(result.isPresent());
        AlertMessage alert = result.get();
        assertEquals(AlertSeverity.CRITICAL_LOW, alert.severity());
        assertEquals("DEVICE-001", alert.deviceId());
        verify(deviceStatusService).updateStatus(eq("DEVICE-001"), eq(DeviceStatus.CRITICAL), contains("CRITICAL_LOW"));
    }

    @Test
    void shouldNotGenerateAlertWhenAlarmStateDoesNotChange() {
        telemetry = createTelemetryPacket(85.0);
        when(alarmThresholdRepository.findByProcessAreaAndMeasurementType(telemetry.processArea(), telemetry.measurementType())).thenReturn(Optional.of(threshold));
        when(alarmStateService.updateAlarmState("DEVICE-001", AlertSeverity.WARNING_HIGH)).thenReturn(AlarmState.NO_CHANGE);
        Optional<AlertMessage> result = alertEvaluationService.evaluate(telemetry);
        assertTrue(result.isEmpty());
        verify(alarmThresholdRepository).findByProcessAreaAndMeasurementType(telemetry.processArea(), telemetry.measurementType());
        verify(alarmStateService).updateAlarmState("DEVICE-001", AlertSeverity.WARNING_HIGH);
        verify(deviceStatusService).updateStatus(eq("DEVICE-001"), eq(DeviceStatus.WARNING), contains("WARNING_HIGH"));
        verifyNoMoreInteractions(alarmThresholdRepository, alarmStateService, deviceStatusService);
    }

    @Test
    void shouldGenerateAlertForNewAlarm() {
        telemetry = createTelemetryPacket(85.0);
        when(alarmThresholdRepository.findByProcessAreaAndMeasurementType(telemetry.processArea(), telemetry.measurementType())).thenReturn(Optional.of(threshold));
        when(alarmStateService.updateAlarmState("DEVICE-001", AlertSeverity.WARNING_HIGH)).thenReturn(AlarmState.NEW_ALARM);
        Optional<AlertMessage> result = alertEvaluationService.evaluate(telemetry);
        assertTrue(result.isPresent());
        AlertMessage alert = result.get();
        assertEquals("DEVICE-001", alert.deviceId());
        assertEquals(AlertSeverity.WARNING_HIGH, alert.severity());
        assertNotNull(alert.alertId());
        assertTrue(alert.message().contains("WARNING_HIGH"));
        verify(alarmThresholdRepository).findByProcessAreaAndMeasurementType(telemetry.processArea(), telemetry.measurementType());
        verify(alarmStateService).updateAlarmState("DEVICE-001", AlertSeverity.WARNING_HIGH);
        verify(deviceStatusService).updateStatus(eq("DEVICE-001"), eq(DeviceStatus.WARNING), contains("WARNING_HIGH"));
        verifyNoMoreInteractions(alarmThresholdRepository, alarmStateService, deviceStatusService);
    }

    private TelemetryPacket createTelemetryPacket(double value) {
        return new TelemetryPacket(
                "DEVICE-001",
                "Temperature Sensor 1",
                DeviceType.TEMPERATURE_SENSOR,
                DeviceStatus.ONLINE,
                "LOC-001",
                "Reactor Zone A",
                ProcessArea.REACTOR_SECTION,
                System.currentTimeMillis(),
                MeasurementType.TEMPERATURE,
                value
        );
    }
}
