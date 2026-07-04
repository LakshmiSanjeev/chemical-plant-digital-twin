package cpdt.simulator;

import cpdt.common.enums.DeviceType;
import cpdt.common.enums.MeasurementType;
import cpdt.common.enums.ProcessArea;
import cpdt.common.models.Location;
import cpdt.simulator.environment.PlantEnvironment;

import cpdt.simulator.utilities.TestSensor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

class SensorDeviceTest {

    private PlantEnvironment plantEnvironment;
    private TestSensor sensor;

    @BeforeEach
    void setUp() {
        plantEnvironment = new PlantEnvironment();
        sensor = new TestSensor("TEMP-001", "Test Sensor",
                 new Location("LOC-001", "Reactor", ProcessArea.REACTOR_SECTION),plantEnvironment);
    }

    @Test
    void constructorShouldInitializeFields() {
        assertEquals("TEMP-001", sensor.getDeviceId());
        assertEquals("Test Sensor", sensor.getName());
        assertEquals(DeviceType.TEMPERATURE_SENSOR, sensor.getType());
        assertEquals(MeasurementType.TEMPERATURE, sensor.getMeasurementType());
        assertEquals(plantEnvironment, sensor.getPlantEnvironment());
        assertTrue(sensor.getLastSampleTimestamp() > 0);
    }

    @Test
    void constructorShouldRejectNullMeasurementType() {
        assertThrows(NullPointerException.class,
                () -> new TestSensor("TEMP-001", "Sensor",
                        new Location("LOC-001", "Reactor", ProcessArea.REACTOR_SECTION),
                        null, plantEnvironment));
    }

    @Test
    void constructorShouldRejectNullPlantEnvironment() {
        assertThrows(NullPointerException.class,
                () -> new TestSensor("TEMP-001", "Sensor",
                        new Location("LOC-001", "Reactor", ProcessArea.REACTOR_SECTION),
                        MeasurementType.TEMPERATURE, null));
    }

    @Test
    void currentValueShouldBeMutable() {
        sensor.setCurrentValue(42.5);
        assertEquals(42.5, sensor.getCurrentValue());
    }

    @Test
    void lastSampleTimestampShouldBeMutable() {
        sensor.setLastSampleTimestamp(12345L);
        assertEquals(12345L, sensor.getLastSampleTimestamp());
    }

    @Test
    void getEnvironmentValueShouldReturnCurrentPlantValue() {
        plantEnvironment.setValue(ProcessArea.REACTOR_SECTION, MeasurementType.TEMPERATURE, 88.5);
        assertEquals(88.5, sensor.readEnvironmentValue());
    }

    @Test
    void calculateHardwareResolutionShouldCalculateCorrectResolution() {
        sensor.calculateResolution();
        double expected = (100.0 - 0.0) / (Math.pow(2, 16) - 1);
        assertEquals(expected, sensor.getResolution(), 1e-12);
    }

    @Test
    void applyAdcQuantizationShouldClampBelowMinimum() {
        double value = sensor.quantize(-50.0);
        assertEquals(0.0, value);
    }

    @Test
    void applyAdcQuantizationShouldClampAboveMaximum() {
        double value = sensor.quantize(500.0);
        assertEquals(100.0, value);
    }

    @Test
    void applyAdcQuantizationShouldKeepValueWithinRange() {
        double value = sensor.quantize(47.2);
        assertTrue(value >= 0.0);
        assertTrue(value <= 100.0);
    }

    @Test
    void updateLongTermDriftShouldExecuteWithoutException() {
        assertDoesNotThrow(() -> sensor.updateDrift(ThreadLocalRandom.current()));
    }

    @Test
    void getReadingShouldReturnEnvironmentValue() {
        plantEnvironment.setValue(ProcessArea.REACTOR_SECTION, MeasurementType.TEMPERATURE, 55.0);
        assertEquals(55.0, sensor.getReading());
    }
}