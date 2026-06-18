package cpdt.simulator.devices;

import cpdt.common.enums.DeviceType;
import cpdt.common.enums.MeasurementType;
import cpdt.common.enums.ProcessArea;
import cpdt.common.models.Location;
import cpdt.simulator.SensorDevice;
import cpdt.simulator.environment.PlantEnvironment;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Smart Piezoresistive Silicon Pressure Transmitter Simulation
 * Fully updated for realistic physical and instrumentation modeling.
 */
public class PressureSensor extends SensorDevice {

    // physical operating limits of the sensor hardware
    private static final double DEFAULT_MIN_RANGE = 0.0;
    private static final double DEFAULT_MAX_RANGE = 40.0;
    // smart transmitter specification
    private static final double BASE_ACCURACY_PERCENT_FS = 0.075;

    // Thermal shifts affect both zero (offset) and span (gain)
    private static final double CALIBRATION_REFERENCE_TEMP_C = 25.0;
    private static final double THERMAL_ZERO_SHIFT_PER_10C = 0.0025; // bar shift per 10C deviation
    private static final double THERMAL_SPAN_SHIFT_PER_10C = 0.0015; // percentage gain shift per 10C deviation

    // ADC properties
    private static final int ADC_RESOLUTION_BITS = 16;

    // Hardware baseline noise floor independent of accuracy calculation
    private static final double ELECTRONIC_NOISE_FLOOR_BAR = 0.0005;

    // internal pressure state
    private double smoothedPressure;
    // first-order damping constant
    private final double dampingTimeConstantSeconds;
    // previous measurement timestamp
    private long lastReadingTimestamp;

    // Track accumulated drift using a continuous Random Walk (Brownian Motion)
    private double accumulatedLongTermDrift = 0.0;
    private static final double DRIFT_VARIANCE_PER_HOUR = 0.0002;

    public PressureSensor(String deviceId, String name, Location location, PlantEnvironment plantEnvironment) {
        super(deviceId, name, DeviceType.PRESSURE_SENSOR, location, MeasurementType.PRESSURE, plantEnvironment);
        Objects.requireNonNull(location, "Location cannot be null");
        this.minRange = DEFAULT_MIN_RANGE;
        this.maxRange = DEFAULT_MAX_RANGE;
        this.resolution = (maxRange - minRange) / (Math.pow(2, ADC_RESOLUTION_BITS) - 1);

        this.lowAlarmLimit = 0.5;
        this.highAlarmLimit = 32.0;
        this.hysteresis = 0.2;
        this.samplingIntervalMs = 500;
        this.accuracy = (BASE_ACCURACY_PERCENT_FS / 100.0) * (maxRange - minRange);

        double initialEnvironmentPressure = plantEnvironment.getValue(location.area(), MeasurementType.PRESSURE);
        this.smoothedPressure = initialEnvironmentPressure;
        this.lastReadingTimestamp = System.currentTimeMillis();
        this.dampingTimeConstantSeconds = determineDampingConstant(location.area());
        setCurrentValue(initialEnvironmentPressure);
    }

    @Override
    public double getReading() {
        long currentTimestamp = System.currentTimeMillis();
        double deltaTimeSeconds = (currentTimestamp - lastReadingTimestamp) / 1000.0;
        if (deltaTimeSeconds <= 0.0) {
            return getCurrentValue();
        }
        lastReadingTimestamp = currentTimestamp;
        double processPressure = getEnvironmentValue();

        double alpha = 1.0 - Math.exp(-deltaTimeSeconds / dampingTimeConstantSeconds);
        smoothedPressure += alpha * (processPressure - smoothedPressure);

        double processTemperature = getPlantEnvironment().getValue(getLocation().area(), MeasurementType.TEMPERATURE);

        ThreadLocalRandom random = ThreadLocalRandom.current();

        double thermalShift = calculateThermalShift(smoothedPressure, processTemperature);
        double electronicNoise = generateElectronicNoise(random);
        double processNoise = generateProcessFluctuation(random);
        updateLongTermDrift(deltaTimeSeconds, random);

        double physicalPressure = smoothedPressure + thermalShift + electronicNoise + processNoise + accumulatedLongTermDrift;

        double digitizedPressure = applyAdcQuantization(physicalPressure);
        digitizedPressure = Math.clamp(digitizedPressure, minRange, maxRange);

        setCurrentValue(digitizedPressure);
        return digitizedPressure;
    }

    private double determineDampingConstant(ProcessArea area) {
        return switch (area) {
            case DISTILLATION_SECTION -> 3.0;
            case REACTOR_SECTION -> 2.0;
            case PIPELINE_SECTION -> 1.5;
            case FEED_SECTION -> 1.0;
            case COOLING_SECTION -> 0.8;
            case STORAGE_SECTION, UTILITIES_SECTION -> 0.5;
        };
    }

    private double calculateThermalShift(double pressure, double temperature) {
        double temperatureDeviation = temperature - CALIBRATION_REFERENCE_TEMP_C;
        double intervalsOfTen = temperatureDeviation / 10.0;

        double zeroShift = intervalsOfTen * THERMAL_ZERO_SHIFT_PER_10C;
        double spanShift = pressure * (intervalsOfTen * THERMAL_SPAN_SHIFT_PER_10C);

        return zeroShift + spanShift;
    }

    private double generateElectronicNoise(ThreadLocalRandom random) {
        return random.nextGaussian() * ELECTRONIC_NOISE_FLOOR_BAR;
    }

    private double generateProcessFluctuation(ThreadLocalRandom random) {
        double fluctuationFactor = switch (getLocation().area()) {
            case PIPELINE_SECTION -> 0.020;
            case COOLING_SECTION -> 0.012;
            case REACTOR_SECTION -> 0.010;
            default -> 0.003;
        };
        return random.nextGaussian() * smoothedPressure * fluctuationFactor;
    }

    private void updateLongTermDrift(double deltaTimeSeconds, ThreadLocalRandom random) {
        double deltaTimeHours = deltaTimeSeconds / 3600.0;
        double stepStandardDeviation = Math.sqrt(deltaTimeHours * DRIFT_VARIANCE_PER_HOUR);
        accumulatedLongTermDrift += random.nextGaussian() * stepStandardDeviation;
    }

    private double applyAdcQuantization(double value) {
        double adcLevels = Math.pow(2, ADC_RESOLUTION_BITS) - 1;
        double normalized = (value - minRange) / (maxRange - minRange);
        normalized = Math.clamp(normalized, 0.0, 1.0);

        double quantized = Math.round(normalized * adcLevels);
        return minRange + (quantized / adcLevels) * (maxRange - minRange);
    }
}
