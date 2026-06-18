package cpdt.common.enums;

import java.util.EnumMap;
import java.util.Map;

public enum ProcessArea {

    REACTOR_SECTION(Map.of(
            MeasurementType.TEMPERATURE, 45.0,
            MeasurementType.PRESSURE, 1.05,
            MeasurementType.HUMIDITY, 30.0,
            MeasurementType.GAS_CONCENTRATION, 8.0
    )),

    STORAGE_SECTION(Map.of(
            MeasurementType.TEMPERATURE, 32.0,
            MeasurementType.PRESSURE, 1.01,
            MeasurementType.HUMIDITY, 50.0,
            MeasurementType.GAS_CONCENTRATION, 1.0
    )),

    FEED_SECTION(Map.of(
            MeasurementType.TEMPERATURE, 38.0,
            MeasurementType.PRESSURE, 1.03,
            MeasurementType.HUMIDITY, 40.0,
            MeasurementType.GAS_CONCENTRATION, 3.0
    )),

    DISTILLATION_SECTION(Map.of(
            MeasurementType.TEMPERATURE, 55.0,
            MeasurementType.PRESSURE, 1.04,
            MeasurementType.HUMIDITY, 25.0,
            MeasurementType.GAS_CONCENTRATION, 5.0
    )),

    COOLING_SECTION(Map.of(
            MeasurementType.TEMPERATURE, 28.0,
            MeasurementType.PRESSURE, 1.01,
            MeasurementType.HUMIDITY, 70.0,
            MeasurementType.GAS_CONCENTRATION, 0.0
    )),

    UTILITIES_SECTION(Map.of(
            MeasurementType.TEMPERATURE, 35.0,
            MeasurementType.PRESSURE, 1.02,
            MeasurementType.HUMIDITY, 45.0,
            MeasurementType.GAS_CONCENTRATION, 1.0
    )),

    PIPELINE_SECTION(Map.of(
            MeasurementType.TEMPERATURE, 40.0,
            MeasurementType.PRESSURE, 1.02,
            MeasurementType.HUMIDITY, 35.0,
            MeasurementType.GAS_CONCENTRATION, 2.0
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