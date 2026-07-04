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
 * Represents a simulated resistance temperature detector
 * similar to a Pt100 RTD used within the chemical plant simulation.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
public class TemperatureSensor extends SensorDevice {

    private static final double DEFAULT_MIN_RANGE = -50.0;
    private static final double DEFAULT_MAX_RANGE = 250.0;
    private static final double IEC_CLASS_A_BASE_ERROR = 0.15;
    private static final double IEC_CLASS_A_TEMP_COEFFICIENT = 0.002;
    private static final double DEFAULT_SELF_HEATING_OFFSET = 0.08;

    private double sensorBodyTemperature;
    private final double thermalTimeConstantSeconds;
    private long lastReadingTimestamp;

    public TemperatureSensor(String deviceId, String name, Location location, PlantEnvironment plantEnvironment) {
        super(deviceId, name, DeviceType.TEMPERATURE_SENSOR, location, MeasurementType.TEMPERATURE, plantEnvironment);
        Objects.requireNonNull(location, "Location cannot be null");

        this.minRange = DEFAULT_MIN_RANGE;
        this.maxRange = DEFAULT_MAX_RANGE;

        calculateHardwareResolution();

        this.driftVariancePerHour = 0.002 * 0.002;

        this.lowAlarmLimit = 0.0;
        this.highAlarmLimit = switch (location.area()) {
            case REACTOR_SECTION, DISTILLATION_SECTION -> 210.0;
            case PIPELINE_SECTION, FEED_SECTION -> 130.0;
            case UTILITIES_SECTION, STORAGE_SECTION -> 85.0;
            case COOLING_SECTION -> 45.0;
        };

        this.accuracy = IEC_CLASS_A_BASE_ERROR;

        this.hysteresis = 1.5;

        this.samplingIntervalMs = 1000;

        double initialEnvironmentTemperature = plantEnvironment.getValue(location.area(), MeasurementType.TEMPERATURE);
        this.sensorBodyTemperature = initialEnvironmentTemperature;
        setCurrentValue(initialEnvironmentTemperature);

        this.lastReadingTimestamp = System.currentTimeMillis();
        this.thermalTimeConstantSeconds = determineThermalTimeConstant(location.area());
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

        double alpha = 1.0 - Math.exp(-deltaTimeSeconds / thermalTimeConstantSeconds);
        sensorBodyTemperature += alpha * (processTemperature - sensorBodyTemperature);

        double heatedTemperature = sensorBodyTemperature + calculateSelfHeating();
        double toleranceBand = IEC_CLASS_A_BASE_ERROR + IEC_CLASS_A_TEMP_COEFFICIENT * Math.abs(heatedTemperature);

        ThreadLocalRandom random = ThreadLocalRandom.current();

        double thermalNoise = random.nextGaussian() * (toleranceBand * 0.20);
        double emiNoise = generateEmiNoise(random);
        double processNoise = generateProcessNoise(processTemperature, random);
        updateLongTermDrift(deltaTimeSeconds, random);
        double physicalTemperature = heatedTemperature + thermalNoise + emiNoise + processNoise + accumulatedLongTermDrift;

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
}
