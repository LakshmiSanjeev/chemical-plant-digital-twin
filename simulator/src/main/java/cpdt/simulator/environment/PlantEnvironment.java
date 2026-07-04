package cpdt.simulator.environment;

import cpdt.common.enums.MeasurementType;
import cpdt.common.enums.ProcessArea;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents the complete physical environment of the simulated chemical plant.
 * <p>
 * The PlantEnvironment maintains a collection of environment models for every
 * process area in the plant and serves as the central source of environmental
 * state. Scenarios modify values through this class, while sensors retrieve
 * environmental conditions to generate realistic telemetry measurements.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
public class PlantEnvironment {

    private final Map<ProcessArea, ProcessAreaEnvironment> processAreaEnvironments;

    public PlantEnvironment() {
        this.processAreaEnvironments = new EnumMap<>(ProcessArea.class);
        initializeProcessAreas();
    }

    private void initializeProcessAreas() {
        for (ProcessArea processArea : ProcessArea.values()) {
            processAreaEnvironments.put(processArea, new ProcessAreaEnvironment(processArea));
        }
    }
    /**
     * Retrieves the current value of the specified environmental measurement
     * from the given process area.
     *
     * @param processArea the process area containing the measurement
     * @param measurementType the type of environmental measurement to retrieve
     * @return the current environmental value
     */
    public double getValue(ProcessArea processArea, MeasurementType measurementType) {
        return getProcessAreaEnvironment(processArea).getEnvironmentalValue(measurementType);
    }
    /**
     * Updates the value of a specific environmental measurement within the
     * given process area.
     *
     * @param processArea the process area whose environment is updated
     * @param measurementType the measurement type to modify
     * @param value the new environmental value
     */
    public void setValue(ProcessArea processArea, MeasurementType measurementType, double value) {
        getProcessAreaEnvironment(processArea).setEnvironmentalValue(measurementType, value);
    }
    /**
     * Returns the environment associated with the specified process area.
     *
     * @param processArea the process area whose environment is requested
     * @return the corresponding process area environment
     * @throws NullPointerException if the process area is null
     * @throws IllegalArgumentException if no environment exists for the process area
     */
    public ProcessAreaEnvironment getProcessAreaEnvironment(ProcessArea processArea) {
        Objects.requireNonNull(processArea, "ProcessArea cannot be null");
        ProcessAreaEnvironment environment = processAreaEnvironments.get(processArea);
        if (environment == null) {
            throw new IllegalArgumentException("No environment found for process area: " + processArea);
        }
        return environment;
    }
    /**
     * Returns an unmodifiable view of all process area environments.
     *
     * @return an immutable map containing every process area environment
     */
    public Map<ProcessArea, ProcessAreaEnvironment> getAllEnvironments() {
        return Collections.unmodifiableMap(processAreaEnvironments);
    }
}