package cpdt.common;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import cpdt.common.enums.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EnumsTest {

    @Test
    void shouldReturnCorrectPrefixes() {
        Assertions.assertEquals("TEMP", DeviceType.TEMPERATURE_SENSOR.getPrefix());
        assertEquals("PRESS", DeviceType.PRESSURE_SENSOR.getPrefix());
        assertEquals("FLOW", DeviceType.FLOW_SENSOR.getPrefix());
        assertEquals("LEVEL", DeviceType.LEVEL_SENSOR.getPrefix());
        assertEquals("GAS", DeviceType.GAS_SENSOR.getPrefix());
        assertEquals("PH", DeviceType.PH_SENSOR.getPrefix());
    }

    @Test
    void shouldHaveUniqueDevicePrefixes() {
        Set<String> prefixes = Arrays.stream(DeviceType.values()).map(DeviceType::getPrefix).collect(Collectors.toSet());
        assertEquals(DeviceType.values().length, prefixes.size());
    }

    @Test
    void shouldLookupDeviceTypeByName() {
        DeviceType type = DeviceType.valueOf("TEMPERATURE_SENSOR");
        assertEquals(DeviceType.TEMPERATURE_SENSOR, type);
    }

    @Test
    void shouldLookupMeasurementTypeByName() {
        MeasurementType type = MeasurementType.valueOf("TEMPERATURE");
        assertEquals(MeasurementType.TEMPERATURE, type);
    }

    @Test
    void shouldHaveNonEmptyDisplayNamesAndUnits() {
        for (MeasurementType type : MeasurementType.values()) {
            assertFalse(type.getDisplayName().isBlank());
            assertFalse(type.getUnit().isBlank());
        }
    }

    @Test
    void shouldLookupAlertSeverityByName() {
        AlertSeverity severity = AlertSeverity.valueOf("CRITICAL_HIGH");
        assertEquals(AlertSeverity.CRITICAL_HIGH, severity);
    }

    @Test
    void shouldHaveUniqueDeviceTypeNames() {
        Set<String> names = Arrays.stream(DeviceType.values()).map(Enum::name).collect(Collectors.toSet());
        assertEquals(DeviceType.values().length, names.size());
    }

    @Test
    void shouldHaveUniqueMeasurementTypeNames() {
        Set<String> names = Arrays.stream(MeasurementType.values()).map(Enum::name).collect(Collectors.toSet());
        assertEquals(MeasurementType.values().length, names.size());
    }

    @Test
    void shouldThrowExceptionForInvalidEnumLookup() {
        assertThrows(IllegalArgumentException.class, () -> DeviceType.valueOf("INVALID_SENSOR"));
        assertThrows(IllegalArgumentException.class, () -> MeasurementType.valueOf("INVALID_MEASUREMENT"));
        assertThrows(IllegalArgumentException.class, () -> DeviceStatus.valueOf("INVALID_STATUS"));
        assertThrows(IllegalArgumentException.class, () -> AlertSeverity.valueOf("INVALID_ALERT"));
        assertThrows(IllegalArgumentException.class, () -> AlarmState.valueOf("INVALID_STATE"));
    }

    @Test
    void shouldThrowExceptionForNullMeasurementType() {
        for (ProcessArea area : ProcessArea.values()) {
            assertThrows(IllegalArgumentException.class, () -> area.getDefaultValue(null));
        }
    }

    @Test
    void shouldReturnPositiveEngineeringValues() {
        for (ProcessArea area : ProcessArea.values()) {
            assertTrue(area.getDefaultValue(MeasurementType.TEMPERATURE) > 0);
            assertTrue(area.getDefaultValue(MeasurementType.PRESSURE) > 0);
            assertTrue(area.getDefaultValue(MeasurementType.HUMIDITY) >= 0);
            assertTrue(area.getDefaultValue(MeasurementType.GAS_CONCENTRATION) >= 0);
            assertTrue(area.getDefaultValue(MeasurementType.FLOW_RATE) >= 0);
            assertTrue(area.getDefaultValue(MeasurementType.LEVEL) >= 0);
        }
    }

    @Test
    void shouldLookupProcessAreaByName() {
        ProcessArea area = ProcessArea.valueOf("REACTOR_SECTION");
        assertEquals(ProcessArea.REACTOR_SECTION, area);
    }

    @Test
    void shouldThrowExceptionForInvalidProcessAreaLookup() {
        assertThrows(IllegalArgumentException.class, () -> ProcessArea.valueOf("INVALID_AREA"));
    }
}