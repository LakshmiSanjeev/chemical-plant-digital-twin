package cpdt.simulator;

import cpdt.common.enums.MeasurementType;
import cpdt.common.enums.ProcessArea;
import cpdt.simulator.environment.PlantEnvironment;
import cpdt.simulator.environment.ProcessAreaEnvironment;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class EnvironmentTest {

    // ProcessAreaEnvironment.java
    @Test
    void constructorShouldInitializeFields() {
        ProcessAreaEnvironment environment = new ProcessAreaEnvironment(ProcessArea.REACTOR_SECTION);
        assertEquals(ProcessArea.REACTOR_SECTION, environment.getProcessArea());
        assertNotNull(environment.getCreatedAt());
        assertNull(environment.getLastUpdated());
    }

    @Test
    void constructorShouldInitializeDefaultValuesForAllSupportedMeasurements() {
        ProcessAreaEnvironment environment = new ProcessAreaEnvironment(ProcessArea.REACTOR_SECTION);
        for (MeasurementType type : environment.getEnvironmentalValues().keySet()) {
            assertEquals(ProcessArea.REACTOR_SECTION.getDefaultValue(type), environment.getEnvironmentalValue(type));
        }
    }

    @Test
    void constructorShouldRejectNullProcessArea() {
        assertThrows(NullPointerException.class, () -> new ProcessAreaEnvironment(null));
    }

    @Test
    void getEnvironmentalValueShouldReturnStoredValue() {
        ProcessAreaEnvironment environment = new ProcessAreaEnvironment(ProcessArea.REACTOR_SECTION);
        double expected = ProcessArea.REACTOR_SECTION.getDefaultValue(MeasurementType.TEMPERATURE);
        assertEquals(expected, environment.getEnvironmentalValue(MeasurementType.TEMPERATURE));
    }

    @Test
    void getEnvironmentalValueShouldRejectNullMeasurementType() {
        ProcessAreaEnvironment environment = new ProcessAreaEnvironment(ProcessArea.REACTOR_SECTION);
        assertThrows(NullPointerException.class, () -> environment.getEnvironmentalValue(null));
    }

    @Test
    void setEnvironmentalValueShouldUpdateValue() {
        ProcessAreaEnvironment environment = new ProcessAreaEnvironment(ProcessArea.REACTOR_SECTION);
        environment.setEnvironmentalValue(MeasurementType.TEMPERATURE, 145.5);
        assertEquals(145.5, environment.getEnvironmentalValue(MeasurementType.TEMPERATURE));
    }

    @Test
    void setEnvironmentalValueShouldUpdateLastUpdatedTimestamp() {
        ProcessAreaEnvironment environment = new ProcessAreaEnvironment(ProcessArea.REACTOR_SECTION);
        assertNull(environment.getLastUpdated());
        environment.setEnvironmentalValue(MeasurementType.PRESSURE, 22.5);
        assertNotNull(environment.getLastUpdated());
        assertFalse(environment.getLastUpdated().isBefore(environment.getCreatedAt()));
    }

    @Test
    void setEnvironmentalValueShouldRejectNullMeasurementType() {
        ProcessAreaEnvironment environment = new ProcessAreaEnvironment(ProcessArea.REACTOR_SECTION);
        assertThrows(NullPointerException.class, () -> environment.setEnvironmentalValue(null, 10.0));
    }

    @Test
    void getEnvironmentalValuesShouldReturnUnmodifiableMap() {
        ProcessAreaEnvironment environment = new ProcessAreaEnvironment(ProcessArea.REACTOR_SECTION);
        Map<MeasurementType, Double> map = environment.getEnvironmentalValues();
        assertThrows(UnsupportedOperationException.class, () -> map.put(MeasurementType.TEMPERATURE, 100.0));
    }

    @Test
    void multipleUpdatesShouldStoreLatestValue() {
        ProcessAreaEnvironment environment = new ProcessAreaEnvironment(ProcessArea.REACTOR_SECTION);
        environment.setEnvironmentalValue(MeasurementType.TEMPERATURE, 50.0);
        environment.setEnvironmentalValue(MeasurementType.TEMPERATURE, 80.0);
        assertEquals(80.0, environment.getEnvironmentalValue(MeasurementType.TEMPERATURE));
    }

    // PlantEnvironment.java
    @Test
    void constructorShouldInitializeAllProcessAreas() {
        PlantEnvironment environment = new PlantEnvironment();
        Map<ProcessArea, ProcessAreaEnvironment> environments = environment.getAllEnvironments();
        assertEquals(ProcessArea.values().length, environments.size());
        for (ProcessArea area : ProcessArea.values()) {
            assertTrue(environments.containsKey(area));
        }
    }

    @Test
    void getProcessAreaEnvironmentShouldReturnEnvironment() {
        PlantEnvironment environment = new PlantEnvironment();
        ProcessAreaEnvironment processAreaEnvironment = environment.getProcessAreaEnvironment(ProcessArea.REACTOR_SECTION);
        assertNotNull(processAreaEnvironment);
        assertEquals(ProcessArea.REACTOR_SECTION, processAreaEnvironment.getProcessArea());
    }

    @Test
    void getProcessAreaEnvironmentShouldRejectNull() {
        PlantEnvironment environment = new PlantEnvironment();
        assertThrows(NullPointerException.class, () -> environment.getProcessAreaEnvironment(null));
    }

    @Test
    void setValueShouldUpdateUnderlyingEnvironment() {
        PlantEnvironment environment = new PlantEnvironment();
        environment.setValue(ProcessArea.REACTOR_SECTION, MeasurementType.TEMPERATURE, 250.0);
        assertEquals(250.0, environment.getValue(ProcessArea.REACTOR_SECTION, MeasurementType.TEMPERATURE));
    }

    @Test
    void setValueShouldOnlyAffectSpecifiedProcessArea() {
        PlantEnvironment environment = new PlantEnvironment();
        environment.setValue(ProcessArea.REACTOR_SECTION, MeasurementType.TEMPERATURE, 300.0);
        assertEquals(300.0, environment.getValue(ProcessArea.REACTOR_SECTION, MeasurementType.TEMPERATURE));
        assertNotEquals(300.0, environment.getValue(ProcessArea.STORAGE_SECTION, MeasurementType.TEMPERATURE));
    }

    @Test
    void getAllEnvironmentsShouldReturnUnmodifiableMap() {
        PlantEnvironment environment = new PlantEnvironment();
        Map<ProcessArea, ProcessAreaEnvironment> map = environment.getAllEnvironments();
        assertThrows(UnsupportedOperationException.class, map::clear);
    }

    @Test
    void getValueShouldReflectLatestSetValue() {
        PlantEnvironment environment = new PlantEnvironment();
        environment.setValue(ProcessArea.PIPELINE_SECTION, MeasurementType.PRESSURE, 42.5);
        assertEquals(42.5, environment.getValue(ProcessArea.PIPELINE_SECTION, MeasurementType.PRESSURE));
    }
}
