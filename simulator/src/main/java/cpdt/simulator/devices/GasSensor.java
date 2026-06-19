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
 * Industrial-grade Electrochemical & Infrared Gas Transmitter Simulation.
 * Fully unified architecture matching the Pressure, Temperature, Level, and Flow designs,
 * while preserving native gas diffusion dynamics, ambient thermal cross-sensitivity,
 * and catalytic/poisoning saturation boundaries.
 */
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

        this.lowAlarmLimit = 10.0;   // Threshold Limit Value (TLV-TWA)
        this.highAlarmLimit = 50.0;  // Immediately Dangerous to Life or Health (IDLH) / Upper explosive limit window

        this.accuracy = (BASE_ACCURACY_PERCENT_FS / 100.0) * (maxRange - minRange);

        this.hysteresis = 1.0;

        this.samplingIntervalMs = 500; // Fast execution required for critical life-safety and leak loops

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
            case PIPELINE_SECTION -> 1.5;     // High velocity, rapid mass transport past the sensor head
            case FEED_SECTION -> 3.0;         // Standard forced ventilation dynamics
            case REACTOR_SECTION -> 4.5;      // Shielded zones, heavy structural interference near vessels
            case DISTILLATION_SECTION -> 5.0; // Complex layout causing stagnation micro-climates
            case COOLING_SECTION -> 6.0;      // High humidity water mist boundary layers delaying diffusion
            case UTILITIES_SECTION -> 4.0;    // Open open-air boiler or compressor bays
            case STORAGE_SECTION -> 8.0;      // Stagnant perimeter air zones, very slow natural gas dispersion
        };
    }

    /**
     * NATIVE PHYSICS: Temperature alters the chemical reaction rates inside electrochemical cells
     * or shifts the narrow bandpass optical filters in infrared detectors.
     */
    private double applyThermalCrossSensitivity(double nominalGas, double currentTemperature) {
        double temperatureDeviation = currentTemperature - CALIBRATION_REFERENCE_TEMP_C;
        double intervalsOfTen = temperatureDeviation / 10.0;

        double zeroShift = intervalsOfTen * THERMAL_ZERO_SHIFT_PER_10C;
        double spanShift = nominalGas * (intervalsOfTen * THERMAL_SPAN_SHIFT_PER_10C);

        return nominalGas + zeroShift + spanShift;
    }

    /**
     * NATIVE PHYSICS: Extreme gas concentrations exhaust the active electrolyte surface chemistry
     * or blind infrared receivers, leading to non-linear saturation scaling and loss of sensitivity.
     */
    private double applySensorSaturationAndPoisoning(double level) {
        if (level > SENSOR_POISONING_THRESHOLD_PPM) {
            double excess = level - SENSOR_POISONING_THRESHOLD_PPM;
            // The sensor exhibits logarithmic compression / degradation past its linear operational limit
            return SENSOR_POISONING_THRESHOLD_PPM + (excess * (1.0 - POISONING_DEGRADATION_FACTOR));
        }
        return level;
    }

    private double generateElectronicNoise(ThreadLocalRandom random) {
        // Continuous baseline instrumentation noise amplifier floor
        return random.nextGaussian() * (accuracy * 0.25);
    }

    private double generatePlumeFluctuationNoise(double currentGas, ThreadLocalRandom random) {
        ProcessArea area = getLocation().area();
        // Air currents, wind speed, and localized turbulence cause gas leaks to arrive in highly erratic "plumes"
        double plumeTurbulenceFactor = switch (area) {
            case PIPELINE_SECTION, UTILITIES_SECTION -> 0.12; // Wind/high drafts break up gas clouds rapidly
            case REACTOR_SECTION, DISTILLATION_SECTION -> 0.08; // Blocked spaces cause pocket concentration cycling
            default -> 0.04; // Enclosed rooms preserve homogeneous concentration profiles
        };
        // Plume noise scales proportionally with the current local gas concentration profile
        return random.nextGaussian() * currentGas * plumeTurbulenceFactor;
    }
}
