package cpdt.simulator.environment;

import cpdt.common.enums.MeasurementType;
import cpdt.common.enums.ProcessArea;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents the physical environmental conditions within a single process area
 * of the simulated chemical plant.
 * <p>
 * Each instance stores the current values of supported environmental
 * measurements such as temperature, pressure, gas concentration, flow rate,
 * pH, humidity, and level. These values form the source of truth observed by
 * sensors and modified by simulation scenarios.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
public class ProcessAreaEnvironment {

    @Getter
    private final ProcessArea processArea;
    private final Map<MeasurementType, Double> environmentalValues;
    @Getter
    private final LocalDateTime createdAt;
    @Getter
    private LocalDateTime lastUpdated;

    private static final Set<MeasurementType> SUPPORTED_MEASUREMENTS =
            EnumSet.of(
                    MeasurementType.TEMPERATURE,
                    MeasurementType.PRESSURE,
                    MeasurementType.HUMIDITY,
                    MeasurementType.GAS_CONCENTRATION,
                    MeasurementType.FLOW_RATE,
                    MeasurementType.PH,
                    MeasurementType.LEVEL
            );
    /**
     * Creates a new environment for the specified process area and initializes
     * all supported measurements with their default values.
     *
     * @param processArea the process area represented by this environment
     * @throws NullPointerException if the process area is null
     */
    public ProcessAreaEnvironment(ProcessArea processArea) {
        Objects.requireNonNull(processArea, "ProcessArea cannot be null");
        this.processArea = processArea;
        this.environmentalValues = new EnumMap<>(MeasurementType.class);
        this.createdAt = LocalDateTime.now();
        this.lastUpdated = null;
        initializeDefaultValues();
    }
    /**
     * Initializes every supported environmental measurement using the default
     * values defined for the associated process area.
     */
    private void initializeDefaultValues() {
        for (MeasurementType type : SUPPORTED_MEASUREMENTS) {
            environmentalValues.put(type, processArea.getDefaultValue(type));
        }
    }
    /**
     * Returns the current value of the specified environmental measurement.
     *
     * @param measurementType the environmental measurement to retrieve
     * @return the current value of the requested measurement
     * @throws NullPointerException if the measurement type is null
     * @throws IllegalArgumentException if the measurement type is unsupported
     */
    public double getEnvironmentalValue(MeasurementType measurementType) {
        validateMeasurementType(measurementType);
        return environmentalValues.get(measurementType);
    }
    /**
     * Updates the value of the specified environmental measurement and records
     * the time of the modification.
     *
     * @param measurementType the environmental measurement to update
     * @param value the new measurement value
     * @throws NullPointerException if the measurement type is null
     * @throws IllegalArgumentException if the measurement type is unsupported
     */
    public void setEnvironmentalValue(MeasurementType measurementType, double value) {
        validateMeasurementType(measurementType);
        environmentalValues.put(measurementType, value);
        this.lastUpdated = LocalDateTime.now();
    }
    /**
     * Validates that the specified measurement type is supported by the
     * environment model.
     *
     * @param measurementType the measurement type to validate
     * @throws NullPointerException if the measurement type is null
     * @throws IllegalArgumentException if the measurement type is unsupported
     */
    private void validateMeasurementType(MeasurementType measurementType) {
        Objects.requireNonNull(measurementType, "MeasurementType cannot be null");
        if (!SUPPORTED_MEASUREMENTS.contains(measurementType)) {
            throw new IllegalArgumentException("Unsupported environmental measurement: " + measurementType);
        }
    }
    /**
     * Returns an unmodifiable view of all environmental measurements maintained
     * by this process area.
     *
     * @return an immutable map of measurement types and their current values
     */
    public Map<MeasurementType, Double> getEnvironmentalValues() {
        return Collections.unmodifiableMap(environmentalValues);
    }
}