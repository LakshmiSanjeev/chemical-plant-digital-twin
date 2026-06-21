package cpdt.simulator.devices;

import cpdt.common.enums.DeviceType;
import cpdt.common.enums.MeasurementType;
import cpdt.common.enums.ProcessArea;
import cpdt.common.models.Location;
import cpdt.simulator.SensorDevice;
import cpdt.simulator.environment.PlantEnvironment;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class GasSensor extends SensorDevice {

    private static final double DEFAULT_MIN_RANGE = 0.0;
    private static final double DEFAULT_MAX_RANGE = 500.0;
    private static final double BASE_ACCURACY_PERCENT_FS = 2.0;
    private static final double CALIBRATION_REFERENCE_TEMP_C = 25.0;
    private static final double THERMAL_ZERO_SHIFT_PER_10C = 0.45;
    private static final double THERMAL_SPAN_SHIFT_PER_10C = 0.015;
    private static final double SENSOR_POISONING_THRESHOLD_PPM = 450.0;
    private static final double POISONING_DEGRADATION_FACTOR = 0.92;

    private double smoothedGasConcentration;
    private final double diffusionTimeConstantSeconds;
    private long lastReadingTimestamp;

    public GasSensor(String deviceId, String name, Location location, PlantEnvironment plantEnvironment) {
        super(deviceId, name, DeviceType.GAS_SENSOR, location, MeasurementType.GAS_CONCENTRATION, plantEnvironment);
        Objects.requireNonNull(location, "Location cannot be null");

        this.minRange = DEFAULT_MIN_RANGE;
        this.maxRange = DEFAULT_MAX_RANGE;

        calculateHardwareResolution();

        this.driftVariancePerHour = 0.015 * 0.015;

        this.lowAlarmLimit = 10.0;
        this.highAlarmLimit = 50.0;

        this.accuracy = (BASE_ACCURACY_PERCENT_FS / 100.0) * (maxRange - minRange);

        this.hysteresis = 1.0;

        this.samplingIntervalMs = 500;

        double initialEnvironmentGas = plantEnvironment.getValue(location.area(), MeasurementType.GAS_CONCENTRATION);
        this.smoothedGasConcentration = initialEnvironmentGas;
        setCurrentValue(initialEnvironmentGas);

        this.lastReadingTimestamp = System.currentTimeMillis();
        this.diffusionTimeConstantSeconds = determineDiffusionConstant(location.area());
    }

    @Override
    public double getReading() {
        long currentTimestamp = System.currentTimeMillis();
        double deltaTimeSeconds = (currentTimestamp - lastReadingTimestamp) / 1000.0;
        if (deltaTimeSeconds <= 0.0) {
            return getCurrentValue();
        }
        lastReadingTimestamp = currentTimestamp;
        double processGasConcentration = getEnvironmentValue();

        double alpha = 1.0 - Math.exp(-deltaTimeSeconds / diffusionTimeConstantSeconds);
        smoothedGasConcentration += alpha * (processGasConcentration - smoothedGasConcentration);

        double processTemperature = getPlantEnvironment().getValue(getLocation().area(), MeasurementType.TEMPERATURE);

        ThreadLocalRandom random = ThreadLocalRandom.current();

        double measuredGas = applyThermalCrossSensitivity(smoothedGasConcentration, processTemperature);
        measuredGas = applySensorSaturationAndPoisoning(measuredGas);

        double sensorElectronicNoise = generateElectronicNoise(random);
        double plumeFluctuationNoise = generatePlumeFluctuationNoise(measuredGas, random);
        updateLongTermDrift(deltaTimeSeconds, random);
        double physicalGasConcentration = measuredGas + sensorElectronicNoise + plumeFluctuationNoise + accumulatedLongTermDrift;

        double digitizedGas = applyAdcQuantization(physicalGasConcentration);
        digitizedGas = Math.clamp(digitizedGas, minRange, maxRange);
        setCurrentValue(digitizedGas);
        return digitizedGas;
    }

    private double determineDiffusionConstant(ProcessArea area) {
        return switch (area) {
            case PIPELINE_SECTION -> 1.5;
            case FEED_SECTION -> 3.0;
            case REACTOR_SECTION -> 4.5;
            case DISTILLATION_SECTION -> 5.0;
            case COOLING_SECTION -> 6.0;
            case UTILITIES_SECTION -> 4.0;
            case STORAGE_SECTION -> 8.0;
        };
    }

    private double applyThermalCrossSensitivity(double nominalGas, double currentTemperature) {
        double temperatureDeviation = currentTemperature - CALIBRATION_REFERENCE_TEMP_C;
        double intervalsOfTen = temperatureDeviation / 10.0;

        double zeroShift = intervalsOfTen * THERMAL_ZERO_SHIFT_PER_10C;
        double spanShift = nominalGas * (intervalsOfTen * THERMAL_SPAN_SHIFT_PER_10C);

        return nominalGas + zeroShift + spanShift;
    }

    private double applySensorSaturationAndPoisoning(double level) {
        if (level > SENSOR_POISONING_THRESHOLD_PPM) {
            double excess = level - SENSOR_POISONING_THRESHOLD_PPM;
            return SENSOR_POISONING_THRESHOLD_PPM + (excess * (1.0 - POISONING_DEGRADATION_FACTOR));
        }
        return level;
    }

    private double generateElectronicNoise(ThreadLocalRandom random) {
        return random.nextGaussian() * (accuracy * 0.25);
    }

    private double generatePlumeFluctuationNoise(double currentGas, ThreadLocalRandom random) {
        ProcessArea area = getLocation().area();
        double plumeTurbulenceFactor = switch (area) {
            case PIPELINE_SECTION, UTILITIES_SECTION -> 0.12;
            case REACTOR_SECTION, DISTILLATION_SECTION -> 0.08;
            default -> 0.04;
        };
        return random.nextGaussian() * currentGas * plumeTurbulenceFactor;
    }
}
