package cpdt.simulator;

import cpdt.common.enums.DeviceStatus;
import cpdt.common.enums.MeasurementType;
import cpdt.common.enums.ProcessArea;
import cpdt.common.models.Location;
import cpdt.simulator.environment.PlantEnvironment;
import cpdt.simulator.scenarios.*;

import cpdt.simulator.utilities.TestSensor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScenarioTest {

    private PlantEnvironment environment;
    private Location reactorLocation;

    @BeforeEach
    void setUp() {
        environment = new PlantEnvironment();
        reactorLocation = new Location("LOC-001", "Reactor", ProcessArea.REACTOR_SECTION);
    }

    @Test
    void overheatScenarioShouldIncreaseAndRestoreTemperature() {
        double baseline = environment.getValue(ProcessArea.REACTOR_SECTION, MeasurementType.TEMPERATURE);

        OverheatScenario scenario = new OverheatScenario("OH-1", ProcessArea.REACTOR_SECTION, environment, 80.0);

        scenario.activate();
        scenario.setActive(true);
        scenario.update(5000);

        double heated = environment.getValue(ProcessArea.REACTOR_SECTION, MeasurementType.TEMPERATURE);
        assertTrue(heated > baseline);

        scenario.deactivate();
        scenario.setActive(false);

        for (int i = 0; i < 3; i++) scenario.update(5000);

        double restored = environment.getValue(ProcessArea.REACTOR_SECTION, MeasurementType.TEMPERATURE);
        assertTrue(restored <= heated);
        assertTrue(scenario.isFullyResolved());
    }

    @Test
    void overpressureScenarioShouldIncreaseAndRestorePressure() {
        double baseline = environment.getValue(ProcessArea.REACTOR_SECTION, MeasurementType.PRESSURE);

        OverpressureScenario scenario = new OverpressureScenario("OP-1", ProcessArea.REACTOR_SECTION, environment, 10.0);

        scenario.activate();
        scenario.setActive(true);
        scenario.update(4000);

        double increased = environment.getValue(ProcessArea.REACTOR_SECTION, MeasurementType.PRESSURE);
        assertTrue(increased > baseline);

        scenario.deactivate();
        scenario.setActive(false);

        for (int i = 0; i < 3; i++) scenario.update(4000);

        double restored = environment.getValue(ProcessArea.REACTOR_SECTION, MeasurementType.PRESSURE);
        assertTrue(restored <= increased);
        assertTrue(scenario.isFullyResolved());
    }

    @Test
    void gasLeakScenarioShouldIncreaseAndRestoreGasConcentration() {
        double baseline = environment.getValue(ProcessArea.REACTOR_SECTION, MeasurementType.GAS_CONCENTRATION);

        GasLeakScenario scenario = new GasLeakScenario("GL-1", ProcessArea.REACTOR_SECTION, environment, 400.0);

        scenario.activate();
        scenario.setActive(true);
        scenario.update(5000);

        double leaked = environment.getValue(ProcessArea.REACTOR_SECTION, MeasurementType.GAS_CONCENTRATION);
        assertTrue(leaked > baseline);

        scenario.deactivate();
        scenario.setActive(false);

        for (int i = 0; i < 3; i++) scenario.update(5000);

        double restored = environment.getValue(ProcessArea.REACTOR_SECTION, MeasurementType.GAS_CONCENTRATION);
        assertTrue(restored <= leaked);
        assertTrue(scenario.isFullyResolved());
    }

    @Test
    void fireScenarioShouldModifyTemperatureAndGasTogether() {
        double baseTemperature = environment.getValue(ProcessArea.REACTOR_SECTION, MeasurementType.TEMPERATURE);
        double baseGas = environment.getValue(ProcessArea.REACTOR_SECTION, MeasurementType.GAS_CONCENTRATION);
        FireScenario scenario = new FireScenario("FIRE-1", ProcessArea.REACTOR_SECTION, environment);

        scenario.activate();
        scenario.setActive(true);
        scenario.update(5000);

        assertTrue(environment.getValue(ProcessArea.REACTOR_SECTION, MeasurementType.TEMPERATURE) > baseTemperature);
        assertTrue(environment.getValue(ProcessArea.REACTOR_SECTION, MeasurementType.GAS_CONCENTRATION) > baseGas);

        scenario.deactivate();
        scenario.setActive(false);

        for (int i = 0; i < 3; i++) scenario.update(5000);
        assertTrue(scenario.isFullyResolved());
    }

    @Test
    void equipmentFailureScenarioShouldChangeSensorStateAndRestoreIt() {
        TestSensor sensor = new TestSensor("TEMP-001", "Temperature", reactorLocation, environment);

        DeviceStatus originalStatus = sensor.getStatus();

        EquipmentFailureScenario scenario = new EquipmentFailureScenario("EQ-1", sensor, environment);

        scenario.activate();

        assertEquals(DeviceStatus.CRITICAL, sensor.getStatus());
        assertEquals(0.0, sensor.getCurrentValue());

        scenario.deactivate();

        assertEquals(originalStatus, sensor.getStatus());
        assertEquals(environment.getValue(ProcessArea.REACTOR_SECTION, MeasurementType.TEMPERATURE), sensor.getCurrentValue());
    }
}