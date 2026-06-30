package cpdt.simulator;

import cpdt.common.enums.DeviceType;
import cpdt.common.enums.MeasurementType;
import cpdt.common.models.Device;
import cpdt.common.models.Location;
import cpdt.simulator.environment.PlantEnvironment;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Base abstraction for all field sensors.
 *
 * <p>Provides the common properties and behavior shared by all sensor
 * implementations, including ADC quantization, hardware resolution
 * calculation, long-term drift simulation, and access to the simulated
 * plant environment. Concrete subclasses extend this functionality by
 * implementing device-specific measurement models and sensor physics.
 *
 * <p>This class follows the Template Method Design pattern by providing
 * shared functionality while delegating sensor-specific measurement
 * logic to subclasses.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */

public abstract class SensorDevice extends Device {

    /**
     * Resolution of the simulated analog-to-digital converter in bits.
     */
    protected static final int ADC_RESOLUTION_BITS = 16;

    @Getter
    private final MeasurementType measurementType;
    @Getter
    @Setter
    private double currentValue;
    @Getter
    protected double minRange;
    @Getter
    protected double maxRange;
    @Getter
    protected double lowAlarmLimit;
    @Getter
    protected double highAlarmLimit;
    @Getter
    protected double hysteresis;
    @Getter
    protected double resolution;
    @Getter
    protected double accuracy;
    @Getter
    protected long samplingIntervalMs;
    @Getter
    @Setter
    private long lastSampleTimestamp;
    @Getter
    private final PlantEnvironment plantEnvironment;

    /**
     * Accumulated sensor drift resulting from long-term operation.
     */
    protected double accumulatedLongTermDrift = 0.0;

    /**
     * Variance of the drift process expressed per hour of operation.
     */
    protected double driftVariancePerHour;

    /**
     * Creates a new sensor device.
     * The parameters {@code deviceId}, {@code name}, {@code type}, and
     * {@code location} have the same meaning as defined by
     * {@link Device#Device(String, String, DeviceType, Location)}.
     * @param measurementType physical quantity measured by the sensor
     * @param plantEnvironment simulated plant environment from which the sensor obtains measurement values
     */
    protected SensorDevice(String deviceId, String name, DeviceType deviceType, Location location,
                           MeasurementType measurementType, PlantEnvironment plantEnvironment) {
        super(deviceId, name, deviceType, location);
        this.measurementType = Objects.requireNonNull(measurementType, "MeasurementType cannot be null");
        this.plantEnvironment = Objects.requireNonNull(plantEnvironment, "PlantEnvironment cannot be null");
        this.lastSampleTimestamp = System.currentTimeMillis();
    }
    /**
     * Obtains the current sensor reading.
     *
     * <p>Concrete sensor implementations must override this method to apply their device-specific measurement model,
     * including any filtering, compensation, noise, drift, and hardware effects.
     *
     * @return the measured value in the engineering units defined by the associated {@code MeasurementType}
     */
    public abstract double getReading();
    /**
     * Retrieves the current environmental value corresponding to this sensor's measurement type and process area.
     *
     * <p>This method provides the underlying physical value that sensor implementations observe before applying
     * device-specific behavior.
     *
     * @return the current environmental value for this sensor
     */
    protected double getEnvironmentValue() {
        Location location = getLocation();
        return plantEnvironment.getValue(location.area(), measurementType);
    }
    /**
     * Applies 16-bit ADC quantization to a measured value.
     *
     * <p>The supplied value is normalized to the sensor's operating range, quantized to the nearest representable
     * ADC level, and converted back to engineering units to simulate the behavior of a real analog-to- digital converter.
     *
     * @param value measured value before quantization
     * @return the quantized measurement
     */
    protected double applyAdcQuantization(double value) {
        double adcLevels = Math.pow(2, ADC_RESOLUTION_BITS) - 1;
        double normalized = (value - minRange) / (maxRange - minRange);
        normalized = Math.clamp(normalized, 0.0, 1.0);
        double quantized = Math.round(normalized * adcLevels);
        return minRange + (quantized / adcLevels) * (maxRange - minRange);
    }
    /**
     * Updates the accumulated long-term sensor drift.
     *
     * <p>Drift is modeled as a Wiener process (Brownian motion), where the drift increment for each sampling interval
     * is drawn from a Gaussian distribution scaled according to the elapsed time and configured drift variance.
     *
     * @param deltaTimeSeconds elapsed time since the previous update, in seconds
     * @param random thread-local random number generator used to sample the drift increment
     */
    protected void updateLongTermDrift(double deltaTimeSeconds, ThreadLocalRandom random) {
        double deltaTimeHours = deltaTimeSeconds / 3600.0;
        double stepStandardDeviation = Math.sqrt(deltaTimeHours * driftVariancePerHour);
        accumulatedLongTermDrift += random.nextGaussian() * stepStandardDeviation;
    }
    /**
     * Calculates the hardware resolution of the sensor.
     *
     * <p>The resolution is determined from the configured measurement
     * range and the simulated ADC resolution, representing the smallest
     * measurable change that can be distinguished by the sensor.
     */
    protected void calculateHardwareResolution() {
        double adcLevels = Math.pow(2, ADC_RESOLUTION_BITS) - 1;
        this.resolution = (this.maxRange - this.minRange) / adcLevels;
    }
}