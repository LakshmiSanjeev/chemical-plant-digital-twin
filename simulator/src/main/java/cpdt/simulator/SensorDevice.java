package cpdt.simulator;

import cpdt.common.enums.DeviceType;
import cpdt.common.enums.MeasurementType;
import cpdt.common.models.Device;
import cpdt.common.models.Location;
import cpdt.simulator.environment.PlantEnvironment;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

public abstract class SensorDevice extends Device {

    protected static final int ADC_RESOLUTION_BITS = 16;

    // by assumption that all sensors process data of a single type.
    @Getter
    private final MeasurementType measurementType;
    // the latest processed data reading
    @Getter
    @Setter
    private double currentValue;
    // range of values obtainable from the sensor under normal working conditions.
    @Getter
    protected double minRange;
    @Getter
    protected double maxRange;
    // software boundaries used to trigger warnings
    @Getter
    protected double lowAlarmLimit;
    @Getter
    protected double highAlarmLimit;
    // used to prevent a sensor from rapidly flipping alarms on and off
    // if the reading hovers precisely on a boundary
    @Getter
    protected double hysteresis;
    // smallest incremental change in the environment
    // that the hardware is capable of detecting
    @Getter
    protected double resolution;
    // maximum error margin
    @Getter
    protected double accuracy;
    // telemetry update interval
    @Getter
    protected long samplingIntervalMs;
    // environmental context
    @Getter
    private final PlantEnvironment plantEnvironment;

    protected SensorDevice(String deviceId, String name, DeviceType deviceType, Location location,
                           MeasurementType measurementType, PlantEnvironment plantEnvironment){
        super(deviceId, name, deviceType, location);
        this.measurementType = Objects.requireNonNull(measurementType, "MeasurementType cannot be null");
        this.plantEnvironment = Objects.requireNonNull(plantEnvironment, "PlantEnvironment cannot be null");
    }

    protected boolean isOutOfPhysicalBounds() {
        return currentValue < minRange || currentValue > maxRange;
    }

    protected double getEnvironmentValue() {
        Location location = getLocation();
        return plantEnvironment.getValue(location.area(), measurementType);
    }

    public abstract double getReading();

    protected double applyAdcQuantization(double value) {
        double adcLevels = Math.pow(2, ADC_RESOLUTION_BITS) - 1;
        double normalized = (value - minRange) / (maxRange - minRange);
        normalized = Math.clamp(normalized, 0.0, 1.0);
        double quantized = Math.round(normalized * adcLevels);
        return minRange + (quantized / adcLevels) * (maxRange - minRange);
    }
}