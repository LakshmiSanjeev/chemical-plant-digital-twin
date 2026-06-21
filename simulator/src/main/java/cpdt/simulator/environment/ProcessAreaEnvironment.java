/*
Each process area in the plant has temperature, pressure, humidity, etc.
These are the actual environmental/process values.
Sensors located there will observe these values and generate noisy/imperfect measurements.
 */

package cpdt.simulator.environment;

import cpdt.common.enums.MeasurementType;
import cpdt.common.enums.ProcessArea;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.*;

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

    public ProcessAreaEnvironment(ProcessArea processArea) {
        Objects.requireNonNull(processArea, "ProcessArea cannot be null");
        this.processArea = processArea;
        this.environmentalValues = new EnumMap<>(MeasurementType.class);
        this.createdAt = LocalDateTime.now();
        this.lastUpdated = null;
        initializeDefaultValues();
    }

    private void initializeDefaultValues() {
        for (MeasurementType type : SUPPORTED_MEASUREMENTS) {
            environmentalValues.put(type, processArea.getDefaultValue(type));
        }
    }

    public double getEnvironmentalValue(MeasurementType measurementType) {
        validateMeasurementType(measurementType);
        return environmentalValues.get(measurementType);
    }

    public void setEnvironmentalValue(MeasurementType measurementType, double value) {
        validateMeasurementType(measurementType);
        environmentalValues.put(measurementType, value);
        this.lastUpdated = LocalDateTime.now();
    }

    private void validateMeasurementType(MeasurementType measurementType) {
        Objects.requireNonNull(measurementType, "MeasurementType cannot be null");
        if (!SUPPORTED_MEASUREMENTS.contains(measurementType)) {
            throw new IllegalArgumentException("Unsupported environmental measurement: " + measurementType);
        }
    }

    public Map<MeasurementType, Double> getEnvironmentalValues() {
        return Collections.unmodifiableMap(environmentalValues);
    }

}