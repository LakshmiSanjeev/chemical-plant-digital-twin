package cpdt.simulator.devices;

import cpdt.common.enums.DeviceType;
import cpdt.common.enums.MeasurementType;
import cpdt.common.enums.ProcessArea;
import cpdt.common.models.Location;
import cpdt.simulator.SensorDevice;
import cpdt.simulator.environment.PlantEnvironment;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class LevelSensor extends SensorDevice {

    private static final double DEFAULT_MIN_RANGE = 0.0;
    private static final double DEFAULT_MAX_RANGE = 12.0;

    private static final double BASE_ACCURACY_PERCENT_FS = 0.05;

    private static final double CALIBRATION_REFERENCE_DIELECTRIC = 1.0;
    private static final double VAPOR_DIELECTRIC_TEMP_COEFFICIENT = 0.0003;

    private static final double RADAR_DEAD_ZONE_METERS = 0.15;

    private double smoothedLevel;
    private final double levelTimeConstantSeconds;
    private long lastReadingTimestamp;

    public LevelSensor(String deviceId, String name, Location location, PlantEnvironment plantEnvironment) {
        super(deviceId, name, DeviceType.LEVEL_SENSOR, location, MeasurementType.LEVEL, plantEnvironment);
        Objects.requireNonNull(location, "Location cannot be null");

        this.minRange = DEFAULT_MIN_RANGE;
        this.maxRange = DEFAULT_MAX_RANGE;

        calculateHardwareResolution();

        this.driftVariancePerHour = 0.0003 * 0.0003;

        this.lowAlarmLimit = 1.0;
        this.highAlarmLimit = 11.0;

        this.accuracy = (BASE_ACCURACY_PERCENT_FS / 100.0) * (maxRange - minRange);

        this.hysteresis = 0.1;

        this.samplingIntervalMs = 1000;

        double initialEnvironmentLevel = plantEnvironment.getValue(location.area(), MeasurementType.LEVEL);
        this.smoothedLevel = initialEnvironmentLevel;
        setCurrentValue(initialEnvironmentLevel);

        this.lastReadingTimestamp = System.currentTimeMillis();
        this.levelTimeConstantSeconds = determineLevelTimeConstant(location.area());
    }

    @Override
    public double getReading() {
        long currentTimestamp = System.currentTimeMillis();
        double deltaTimeSeconds = (currentTimestamp - lastReadingTimestamp) / 1000.0;
        if (deltaTimeSeconds <= 0.0) {
            return getCurrentValue();
        }
        lastReadingTimestamp = currentTimestamp;
        double processLevel = getEnvironmentValue();

        double alpha = 1.0 - Math.exp(-deltaTimeSeconds / levelTimeConstantSeconds);
        smoothedLevel += alpha * (processLevel - smoothedLevel);

        double processTemperature = getPlantEnvironment().getValue(getLocation().area(), MeasurementType.TEMPERATURE);

        ThreadLocalRandom random = ThreadLocalRandom.current();

        double measuredLevel = applyVaporDielectricCorrection(smoothedLevel, processTemperature);

        measuredLevel = applyPhysicalDeadZones(measuredLevel);

        double radarElectronicNoise = generateElectronicNoise(random);
        double surfaceSloshNoise = generateSurfaceSloshNoise(measuredLevel, random);
        updateLongTermDrift(deltaTimeSeconds, random);

        double physicalLevel = measuredLevel + radarElectronicNoise + surfaceSloshNoise + accumulatedLongTermDrift;
        double digitizedLevel = applyAdcQuantization(physicalLevel);
        digitizedLevel = Math.clamp(digitizedLevel, minRange, maxRange);
        setCurrentValue(digitizedLevel);
        return digitizedLevel;
    }

    private double determineLevelTimeConstant(ProcessArea area) {
        return switch (area) {
            case STORAGE_SECTION -> 60.0;
            case DISTILLATION_SECTION -> 15.0;
            case REACTOR_SECTION -> 10.0;
            case FEED_SECTION -> 8.0;
            case UTILITIES_SECTION -> 20.0;
            case COOLING_SECTION -> 25.0;
            case PIPELINE_SECTION -> 2.0;
        };
    }

    private double applyVaporDielectricCorrection(double nominalLevel, double currentTemperature) {
        double temperatureDeviation = Math.max(0.0, currentTemperature - 25.0);
        double vaporDielectric = CALIBRATION_REFERENCE_DIELECTRIC + (temperatureDeviation * VAPOR_DIELECTRIC_TEMP_COEFFICIENT);

        double totalUllageSpace = maxRange - nominalLevel;
        double apparentUllageSpace = totalUllageSpace * Math.sqrt(vaporDielectric);
        return maxRange - apparentUllageSpace;
    }

    private double applyPhysicalDeadZones(double level) {
        if (level > (maxRange - RADAR_DEAD_ZONE_METERS)) {
            return maxRange - RADAR_DEAD_ZONE_METERS;
        }
        if (level < (minRange + RADAR_DEAD_ZONE_METERS)) {
            return minRange;
        }
        return level;
    }

    private double generateElectronicNoise(ThreadLocalRandom random) {
        return random.nextGaussian() * (accuracy * 0.20);
    }

    private double generateSurfaceSloshNoise(double currentLevel, ThreadLocalRandom random) {
        ProcessArea area = getLocation().area();
        double sloshIntensity = switch (area) {
            case REACTOR_SECTION -> 0.080;
            case DISTILLATION_SECTION -> 0.045;
            case PIPELINE_SECTION -> 0.030;
            case FEED_SECTION -> 0.020;
            default -> 0.005;
        };
        double normalizationFactor = Math.sin(Math.PI * (currentLevel / maxRange));
        return random.nextGaussian() * sloshIntensity * Math.abs(normalizationFactor);
    }
}
