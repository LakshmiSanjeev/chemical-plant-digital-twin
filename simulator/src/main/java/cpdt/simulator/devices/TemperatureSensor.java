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
 * Industrial-grade RTD Temperature Sensor Simulation
 * Fully unified architecture matching the updated PressureSensor design,
 * while preserving native RTD physics and IEC 60751 Class A specifications.
 */
public class TemperatureSensor extends SensorDevice {

    // physical operating limits of the sensor hardware
    private static final double DEFAULT_MIN_RANGE = -50.0;
    private static final double DEFAULT_MAX_RANGE = 250.0;
    // IEC 60751 Class A RTD approximation
    private static final double IEC_CLASS_A_BASE_ERROR = 0.15;
    private static final double IEC_CLASS_A_TEMP_COEFFICIENT = 0.002;
    // RTD self-heating effect
    private static final double DEFAULT_SELF_HEATING_OFFSET = 0.08;

    // NATIVE PHYSICS PRESERVED: Variance calculated from the original baseline drift value
    private static final double DRIFT_VARIANCE_PER_HOUR = 0.002 * 0.002;

    // internal thermal state of sensing element
    private double sensorBodyTemperature;
    // thermal inertia / thermowell lag constant
    private final double thermalTimeConstantSeconds;
    // previous measurement timestamp
    private long lastReadingTimestamp;

    // UNIFORMITY CORRECTION: Stateful continuous Random Walk tracking
    private double accumulatedLongTermDrift = 0.0;

    public TemperatureSensor(String deviceId, String name, Location location, PlantEnvironment plantEnvironment) {
        super(deviceId, name, DeviceType.TEMPERATURE_SENSOR, location, MeasurementType.TEMPERATURE, plantEnvironment);
        Objects.requireNonNull(location, "Location cannot be null");
        this.minRange = DEFAULT_MIN_RANGE;
        this.maxRange = DEFAULT_MAX_RANGE;

        // UNIFORMITY CORRECTION: Native 16-bit physical hardware LSB step size
        this.resolution = (maxRange - minRange) / (Math.pow(2, ADC_RESOLUTION_BITS) - 1);

        this.lowAlarmLimit = 0.0;
        this.highAlarmLimit = 120.0;
        this.accuracy = 0.25;
        this.hysteresis = 1.5;
        this.samplingIntervalMs = 1000;

        double initialEnvironmentTemperature = plantEnvironment.getValue(location.area(), MeasurementType.TEMPERATURE);
        this.sensorBodyTemperature = initialEnvironmentTemperature;
        this.lastReadingTimestamp = System.currentTimeMillis();
        this.thermalTimeConstantSeconds = determineThermalTimeConstant(location.area());
        setCurrentValue(initialEnvironmentTemperature);
    }

    @Override
    public double getReading() {
        long currentTimestamp = System.currentTimeMillis();
        double deltaTimeSeconds = (currentTimestamp - lastReadingTimestamp) / 1000.0;
        if (deltaTimeSeconds <= 0.0) {
            return getCurrentValue();
        }
        lastReadingTimestamp = currentTimestamp;
        double processTemperature = getEnvironmentValue();

        // Exponential Moving Average (EMA) filter
        double alpha = 1.0 - Math.exp(-deltaTimeSeconds / thermalTimeConstantSeconds);
        sensorBodyTemperature += alpha * (processTemperature - sensorBodyTemperature);

        // Native self-heating mathematics preserved
        double heatedTemperature = sensorBodyTemperature + calculateSelfHeating();

        // Native IEC 60751 Class A tolerance band math preserved
        double toleranceBand = IEC_CLASS_A_BASE_ERROR + IEC_CLASS_A_TEMP_COEFFICIENT * Math.abs(heatedTemperature);

        ThreadLocalRandom random = ThreadLocalRandom.current();

        // Native noise profiles preserved
        double thermalNoise = random.nextGaussian() * (toleranceBand * 0.20);
        double emiNoise = generateEmiNoise(random);
        double processNoise = generateProcessNoise(processTemperature, random);

        // UNIFORMITY CORRECTION: Stateful drift step execution
        updateLongTermDrift(deltaTimeSeconds, random);

        double physicalTemperature = heatedTemperature + thermalNoise + emiNoise + processNoise + accumulatedLongTermDrift;

        // UNIFORMITY CORRECTION: Single-layer physical ADC quantization tracking with hard limits
        double digitizedTemperature = applyAdcQuantization(physicalTemperature);
        digitizedTemperature = Math.clamp(digitizedTemperature, minRange, maxRange);

        setCurrentValue(digitizedTemperature);
        return digitizedTemperature;
    }

    private double determineThermalTimeConstant(ProcessArea area) {
        return switch (area) {
            case REACTOR_SECTION -> 45.0;
            case PIPELINE_SECTION -> 12.0;
            case FEED_SECTION -> 18.0;
            case DISTILLATION_SECTION -> 35.0;
            case UTILITIES_SECTION -> 25.0;
            case STORAGE_SECTION -> 30.0;
            case COOLING_SECTION -> 10.0;
        };
    }

    private double calculateSelfHeating() {
        ProcessArea area = getLocation().area();
        return switch (area) {
            case PIPELINE_SECTION, COOLING_SECTION -> 0.03;
            case STORAGE_SECTION, UTILITIES_SECTION -> 0.10;
            default -> DEFAULT_SELF_HEATING_OFFSET;
        };
    }

    private double generateEmiNoise(ThreadLocalRandom random) {
        ProcessArea area = getLocation().area();
        double noiseScale = switch (area) {
            case UTILITIES_SECTION -> 0.08;
            case REACTOR_SECTION, DISTILLATION_SECTION -> 0.05;
            default -> 0.02;
        };
        return random.nextGaussian() * noiseScale;
    }

    private double generateProcessNoise(double processTemperature, ThreadLocalRandom random) {
        ProcessArea area = getLocation().area();
        double turbulenceFactor = switch (area) {
            case REACTOR_SECTION -> 0.12;
            case DISTILLATION_SECTION -> 0.10;
            case PIPELINE_SECTION -> 0.07;
            default -> 0.03;
        };
        return random.nextGaussian() * turbulenceFactor * (processTemperature / 100.0);
    }

    // UNIFORMITY CORRECTION: Incremental random walk calculation matching pressure class architecture
    private void updateLongTermDrift(double deltaTimeSeconds, ThreadLocalRandom random) {
        double deltaTimeHours = deltaTimeSeconds / 3600.0;
        double stepStandardDeviation = Math.sqrt(deltaTimeHours * DRIFT_VARIANCE_PER_HOUR);
        accumulatedLongTermDrift += random.nextGaussian() * stepStandardDeviation;
    }
}
