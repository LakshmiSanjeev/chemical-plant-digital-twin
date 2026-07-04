package cpdt.simulator;

import cpdt.common.enums.DeviceType;
import cpdt.common.enums.MeasurementType;
import cpdt.common.enums.ProcessArea;
import cpdt.common.models.Location;
import cpdt.simulator.devices.*;
import cpdt.simulator.environment.PlantEnvironment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class SensorSubclassTest {

    private PlantEnvironment environment;
    private Location reactorLocation;

    @BeforeEach
    void setUp() {
        environment = new PlantEnvironment();
        reactorLocation = new Location("LOC-001", "Reactor", ProcessArea.REACTOR_SECTION);
    }

    private void rewindTime(Object sensor) throws Exception {
        Field field = sensor.getClass().getDeclaredField("lastReadingTimestamp");
        field.setAccessible(true);
        field.setLong(sensor, System.currentTimeMillis() - 5000);
    }

    @Test
    void temperatureSensorShouldBehaveCorrectly() throws Exception {
        TemperatureSensor sensor = new TemperatureSensor("TEMP-001", "Temperature Sensor", reactorLocation, environment);

        // Constructor validation
        assertEquals(DeviceType.TEMPERATURE_SENSOR, sensor.getType());
        assertEquals(MeasurementType.TEMPERATURE, sensor.getMeasurementType());
        assertEquals(environment, sensor.getPlantEnvironment());
        assertEquals(environment.getValue(ProcessArea.REACTOR_SECTION, MeasurementType.TEMPERATURE), sensor.getCurrentValue());

        rewindTime(sensor);

        double reading = sensor.getReading();

        // Reading validation
        assertFalse(Double.isNaN(reading));
        assertFalse(Double.isInfinite(reading));
        assertTrue(reading >= sensor.getMinRange());
        assertTrue(reading <= sensor.getMaxRange());
        assertEquals(reading, sensor.getCurrentValue());

        // Environment response
        environment.setValue(ProcessArea.REACTOR_SECTION, MeasurementType.TEMPERATURE, 120.0);

        rewindTime(sensor);

        double updated = sensor.getReading();

        assertNotEquals(reading, updated);

        // Stability
        for (int i = 0; i < 25; i++) {
            rewindTime(sensor);
            double value = sensor.getReading();
            assertFalse(Double.isNaN(value));
            assertFalse(Double.isInfinite(value));
            assertTrue(value >= sensor.getMinRange());
            assertTrue(value <= sensor.getMaxRange());
        }
    }

    @Test
    void pressureSensorShouldBehaveCorrectly() throws Exception {
        PressureSensor sensor = new PressureSensor("PRESS-001", "Pressure Sensor", reactorLocation, environment);

        // Constructor validation
        assertEquals(DeviceType.PRESSURE_SENSOR, sensor.getType());
        assertEquals(MeasurementType.PRESSURE, sensor.getMeasurementType());
        assertEquals(environment, sensor.getPlantEnvironment());

        assertEquals(environment.getValue(ProcessArea.REACTOR_SECTION, MeasurementType.PRESSURE), sensor.getCurrentValue());

        rewindTime(sensor);

        double reading = sensor.getReading();

        // Reading validation
        assertFalse(Double.isNaN(reading));
        assertFalse(Double.isInfinite(reading));
        assertTrue(reading >= sensor.getMinRange());
        assertTrue(reading <= sensor.getMaxRange());
        assertEquals(reading, sensor.getCurrentValue());

        // Environment response
        environment.setValue(ProcessArea.REACTOR_SECTION, MeasurementType.PRESSURE, 18.0);

        rewindTime(sensor);

        double updated = sensor.getReading();

        assertNotEquals(reading, updated);

        // Stability
        for (int i = 0; i < 25; i++) {
            rewindTime(sensor);
            double value = sensor.getReading();
            assertFalse(Double.isNaN(value));
            assertFalse(Double.isInfinite(value));
            assertTrue(value >= sensor.getMinRange());
            assertTrue(value <= sensor.getMaxRange());
        }
    }

    @Test
    void flowSensorShouldBehaveCorrectly() throws Exception {
        FlowSensor sensor = new FlowSensor("FLOW-001", "Flow Sensor", reactorLocation, environment);

        // Constructor validation
        assertEquals(DeviceType.FLOW_SENSOR, sensor.getType());
        assertEquals(MeasurementType.FLOW_RATE, sensor.getMeasurementType());
        assertEquals(environment, sensor.getPlantEnvironment());
        assertEquals(environment.getValue(ProcessArea.REACTOR_SECTION, MeasurementType.FLOW_RATE), sensor.getCurrentValue());

        rewindTime(sensor);

        double reading = sensor.getReading();

        // Reading validation
        assertFalse(Double.isNaN(reading));
        assertFalse(Double.isInfinite(reading));
        assertTrue(reading >= sensor.getMinRange());
        assertTrue(reading <= sensor.getMaxRange());
        assertEquals(reading, sensor.getCurrentValue());

        // Environment response
        environment.setValue(ProcessArea.REACTOR_SECTION, MeasurementType.FLOW_RATE, 120.0);

        rewindTime(sensor);

        double updated = sensor.getReading();

        assertNotEquals(reading, updated);

        // Stability
        for (int i = 0; i < 25; i++) {
            rewindTime(sensor);
            double value = sensor.getReading();
            assertFalse(Double.isNaN(value));
            assertFalse(Double.isInfinite(value));
            assertTrue(value >= sensor.getMinRange());
            assertTrue(value <= sensor.getMaxRange());
        }
    }

    @Test
    void levelSensorShouldBehaveCorrectly() throws Exception {
        LevelSensor sensor = new LevelSensor("LEVEL-001", "Level Sensor", reactorLocation, environment);

        // Constructor validation
        assertEquals(DeviceType.LEVEL_SENSOR, sensor.getType());
        assertEquals(MeasurementType.LEVEL, sensor.getMeasurementType());
        assertEquals(environment, sensor.getPlantEnvironment());
        assertEquals(environment.getValue(ProcessArea.REACTOR_SECTION, MeasurementType.LEVEL), sensor.getCurrentValue());

        rewindTime(sensor);

        double reading = sensor.getReading();

        // Reading validation
        assertFalse(Double.isNaN(reading));
        assertFalse(Double.isInfinite(reading));
        assertTrue(reading >= sensor.getMinRange());
        assertTrue(reading <= sensor.getMaxRange());
        assertEquals(reading, sensor.getCurrentValue());

        // Environment response
        environment.setValue(ProcessArea.REACTOR_SECTION, MeasurementType.LEVEL, 10.5);

        rewindTime(sensor);

        double updated = sensor.getReading();

        assertNotEquals(reading, updated);

        // Stability
        for (int i = 0; i < 25; i++) {
            rewindTime(sensor);
            double value = sensor.getReading();
            assertFalse(Double.isNaN(value));
            assertFalse(Double.isInfinite(value));
            assertTrue(value >= sensor.getMinRange());
            assertTrue(value <= sensor.getMaxRange());
        }
    }

    @Test
    void gasSensorShouldBehaveCorrectly() throws Exception {
        GasSensor sensor = new GasSensor("GAS-001", "Gas Sensor", reactorLocation, environment);

        // Constructor validation
        assertEquals(DeviceType.GAS_SENSOR, sensor.getType());
        assertEquals(MeasurementType.GAS_CONCENTRATION, sensor.getMeasurementType());
        assertEquals(environment, sensor.getPlantEnvironment());
        assertEquals(environment.getValue(ProcessArea.REACTOR_SECTION, MeasurementType.GAS_CONCENTRATION), sensor.getCurrentValue());

        rewindTime(sensor);

        double reading = sensor.getReading();

        // Reading validation
        assertFalse(Double.isNaN(reading));
        assertFalse(Double.isInfinite(reading));
        assertTrue(reading >= sensor.getMinRange());
        assertTrue(reading <= sensor.getMaxRange());
        assertEquals(reading, sensor.getCurrentValue());

        // Environment response
        environment.setValue(ProcessArea.REACTOR_SECTION, MeasurementType.GAS_CONCENTRATION, 250.0);

        rewindTime(sensor);

        double updated = sensor.getReading();

        assertNotEquals(reading, updated);

        // Stability
        for (int i = 0; i < 25; i++) {
            rewindTime(sensor);
            double value = sensor.getReading();
            assertFalse(Double.isNaN(value));
            assertFalse(Double.isInfinite(value));
            assertTrue(value >= sensor.getMinRange());
            assertTrue(value <= sensor.getMaxRange());
        }
    }

    @Test
    void phSensorShouldBehaveCorrectly() throws Exception {

        PhSensor sensor = new PhSensor("PH-001", "pH Sensor", reactorLocation, environment);

        // Constructor validation
        assertEquals(DeviceType.PH_SENSOR, sensor.getType());
        assertEquals(MeasurementType.PH, sensor.getMeasurementType());
        assertEquals(environment, sensor.getPlantEnvironment());

        assertEquals(environment.getValue(ProcessArea.REACTOR_SECTION, MeasurementType.PH), sensor.getCurrentValue());

        rewindTime(sensor);

        double reading = sensor.getReading();

        // Reading validation
        assertFalse(Double.isNaN(reading));
        assertFalse(Double.isInfinite(reading));
        assertTrue(reading >= sensor.getMinRange());
        assertTrue(reading <= sensor.getMaxRange());
        assertEquals(reading, sensor.getCurrentValue());

        // Environment response
        environment.setValue(ProcessArea.REACTOR_SECTION, MeasurementType.PH, 9.5);

        rewindTime(sensor);

        double updated = sensor.getReading();

        assertNotEquals(reading, updated);

        // Stability
        for (int i = 0; i < 25; i++) {
            rewindTime(sensor);
            double value = sensor.getReading();
            assertFalse(Double.isNaN(value));
            assertFalse(Double.isInfinite(value));
            assertTrue(value >= sensor.getMinRange());
            assertTrue(value <= sensor.getMaxRange());
        }
    }
}