package cpdt.common.enums;

import java.util.EnumMap;
import java.util.Map;

/**
 * Represents the distinct process areas within the simulated chemical plant.
 *
 * <p>Each process area maintains a predefined set of baseline
 * environmental values for every supported {@link MeasurementType}.
 * These values are used to initialize the plant environment and serve as
 * the nominal operating conditions observed by sensors before scenarios
 * modify them.</p>
 *
 * <p>The default values defined for each process area are illustrative and
 * may be customized to match the characteristics of a specific real-world
 * process. Consequently, the meaning and operating conditions associated
 * with each enum constant depend on the plant or system being simulated.</p>
 *
 * @since 1.0
 */
public enum ProcessArea {

    REACTOR_SECTION(Map.of(
            MeasurementType.TEMPERATURE, 45.0,
            MeasurementType.PRESSURE, 1.05,
            MeasurementType.HUMIDITY, 30.0,
            MeasurementType.GAS_CONCENTRATION, 8.0,
            MeasurementType.FLOW_RATE, 95.0,
            MeasurementType.PH, 6.8,
            MeasurementType.LEVEL, 8.5
    )),

    STORAGE_SECTION(Map.of(
            MeasurementType.TEMPERATURE, 32.0,
            MeasurementType.PRESSURE, 1.01,
            MeasurementType.HUMIDITY, 50.0,
            MeasurementType.GAS_CONCENTRATION, 1.0,
            MeasurementType.FLOW_RATE, 5.0,
            MeasurementType.PH, 7.0,
            MeasurementType.LEVEL, 9.0
    )),

    FEED_SECTION(Map.of(
            MeasurementType.TEMPERATURE, 38.0,
            MeasurementType.PRESSURE, 1.03,
            MeasurementType.HUMIDITY, 40.0,
            MeasurementType.GAS_CONCENTRATION, 3.0,
            MeasurementType.FLOW_RATE, 120.0,
            MeasurementType.PH, 6.5,
            MeasurementType.LEVEL, 6.0
    )),

    DISTILLATION_SECTION(Map.of(
            MeasurementType.TEMPERATURE, 55.0,
            MeasurementType.PRESSURE, 1.04,
            MeasurementType.HUMIDITY, 25.0,
            MeasurementType.GAS_CONCENTRATION, 5.0,
            MeasurementType.FLOW_RATE, 80.0,
            MeasurementType.PH, 5.8,
            MeasurementType.LEVEL, 7.5
    )),

    COOLING_SECTION(Map.of(
            MeasurementType.TEMPERATURE, 28.0,
            MeasurementType.PRESSURE, 1.01,
            MeasurementType.HUMIDITY, 70.0,
            MeasurementType.GAS_CONCENTRATION, 0.0,
            MeasurementType.FLOW_RATE, 140.0,
            MeasurementType.PH, 7.2,
            MeasurementType.LEVEL, 5.0
    )),

    UTILITIES_SECTION(Map.of(
            MeasurementType.TEMPERATURE, 35.0,
            MeasurementType.PRESSURE, 1.02,
            MeasurementType.HUMIDITY, 45.0,
            MeasurementType.GAS_CONCENTRATION, 1.0,
            MeasurementType.FLOW_RATE, 60.0,
            MeasurementType.PH, 7.0,
            MeasurementType.LEVEL, 4.0
    )),

    PIPELINE_SECTION(Map.of(
            MeasurementType.TEMPERATURE, 40.0,
            MeasurementType.PRESSURE, 1.02,
            MeasurementType.HUMIDITY, 35.0,
            MeasurementType.GAS_CONCENTRATION, 2.0,
            MeasurementType.FLOW_RATE, 130.0,
            MeasurementType.PH, 6.7,
            MeasurementType.LEVEL, 2.0
    ));

    private final Map<MeasurementType, Double> defaultValues;

    ProcessArea(Map<MeasurementType, Double> values) {
        this.defaultValues = new EnumMap<>(MeasurementType.class);
        this.defaultValues.putAll(values);
    }

    public double getDefaultValue(MeasurementType type) {
        Double value = defaultValues.get(type);
        if (value == null) {
            throw new IllegalArgumentException("Unsupported measurement type: " + type);
        }
        return value;
    }
}