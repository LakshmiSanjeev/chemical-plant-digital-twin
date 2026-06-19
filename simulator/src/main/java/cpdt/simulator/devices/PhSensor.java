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
 * Industrial-grade Glass Electrode pH & Analytical Transmitter Simulation.
 * Fully unified architecture matching the Pressure, Temperature, Level, Flow, and Gas designs,
 * while preserving native Nernstian temperature-dependent electrochemistry, glass impedance
 * thermal loading, and reference junction fouling/poisoning saturation profiles.
 */
public class PhSensor extends SensorDevice {

    private static final double DEFAULT_MIN_RANGE = 0.0;
    private static final double DEFAULT_MAX_RANGE = 14.0;
    private static final double BASE_ACCURACY_PH = 0.02;
    private static final double CALIBRATION_REFERENCE_TEMP_C = 25.0;
    private static final double ABSOLUTE_ZERO_KELVIN = 273.15;
    private static final double THERMAL_SLOPE_SHIFT_COEFFICIENT = 0.0033;
    private static final double SENSITIVITY_DEGRADATION_THRESHOLD_PH = 12.0;
    private static final double FOULING_COMPRESSION_FACTOR = 0.95;

    private double smoothedPhValue;
    private final double chemicalResponseTimeConstantSeconds;
    private long lastReadingTimestamp;

    public PhSensor(String deviceId, String name, Location location, PlantEnvironment plantEnvironment) {
        super(deviceId, name, DeviceType.PH_SENSOR, location, MeasurementType.PH, plantEnvironment);
        Objects.requireNonNull(location, "Location cannot be null");

        this.minRange = DEFAULT_MIN_RANGE;
        this.maxRange = DEFAULT_MAX_RANGE;

        calculateHardwareResolution();

        this.driftVariancePerHour = 0.001 * 0.001;

        this.lowAlarmLimit = 3.5;    // Acidic breakthrough threshold
        this.highAlarmLimit = 10.5;  // Alkaline scaling / caustic warning boundary

        this.accuracy = BASE_ACCURACY_PH;

        this.hysteresis = 0.15;

        this.samplingIntervalMs = 500; // Fast sampling required for tight chemical dosage / titration loops

        double initialEnvironmentPh = plantEnvironment.getValue(location.area(), MeasurementType.PH);
        this.smoothedPhValue = initialEnvironmentPh;
        setCurrentValue(initialEnvironmentPh);

        this.lastReadingTimestamp = System.currentTimeMillis();
        this.chemicalResponseTimeConstantSeconds = determineChemicalTimeConstant(location.area());
    }

    @Override
    public double getReading() {
        long currentTimestamp = System.currentTimeMillis();
        double deltaTimeSeconds = (currentTimestamp - lastReadingTimestamp) / 1000.0;
        if (deltaTimeSeconds <= 0.0) {
            return getCurrentValue();
        }
        lastReadingTimestamp = currentTimestamp;
        double processPh = getEnvironmentValue();

        double alpha = 1.0 - Math.exp(-deltaTimeSeconds / chemicalResponseTimeConstantSeconds);
        smoothedPhValue += alpha * (processPh - smoothedPhValue);

        double processTemperature = getPlantEnvironment().getValue(getLocation().area(), MeasurementType.TEMPERATURE);

        ThreadLocalRandom random = ThreadLocalRandom.current();

        double measuredPh = applyNernstTemperatureCompensation(smoothedPhValue, processTemperature);
        measuredPh = applyElectrodeFoulingAndPoisoning(measuredPh);

        double highImpedancePreampNoise = generatePreampNoise(random);
        double processStreamingCurrentNoise = generateStreamingCurrentNoise(processTemperature, random);
        updateLongTermDrift(deltaTimeSeconds, random);
        double physicalPh = measuredPh + highImpedancePreampNoise + processStreamingCurrentNoise + accumulatedLongTermDrift;

        double digitizedPh = applyAdcQuantization(physicalPh);
        digitizedPh = Math.clamp(digitizedPh, minRange, maxRange);
        setCurrentValue(digitizedPh);
        return digitizedPh;
    }

    private double determineChemicalTimeConstant(ProcessArea area) {
        return switch (area) {
            case PIPELINE_SECTION -> 2.0;      // High velocity dynamic inline blending
            case FEED_SECTION -> 4.0;          // Forced influent treatment streams
            case REACTOR_SECTION -> 8.0;       // Buffer delays near tank walls away from impeller
            case DISTILLATION_SECTION -> 12.0; // High viscosity fraction accumulation traps
            case COOLING_SECTION -> 15.0;      // Bio-film build-up dampening in open basin channels
            case UTILITIES_SECTION -> 10.0;     // Boiler feedwater demineralization return loops
            case STORAGE_SECTION -> 45.0;      // Large volume stagnant inventory equalization
        };
    }

    /**
     * NATIVE PHYSICS: Electrochemistry follows the Nernst Equation ($E = E_0 - \frac{RT}{nF}\ln Q$).
     * Sensor voltage output slope varies proportionally with absolute thermal loading in Kelvin.
     * Measurement error scales with absolute distance from the chemical neutral point (7.0 pH Isopotential).
     */
    private double applyNernstTemperatureCompensation(double nominalPh, double currentTemperature) {
        double currentKelvin = currentTemperature + ABSOLUTE_ZERO_KELVIN;
        double referenceKelvin = CALIBRATION_REFERENCE_TEMP_C + ABSOLUTE_ZERO_KELVIN;

        // Quantify the thermal scaling coefficient change on EMF generation
        double slopeThermalFactor = currentKelvin / referenceKelvin;

        // Deviation expands further away from the 7.0 pH zero-voltage balance point
        double deviationFromIsopotential = nominalPh - 7.0;
        double uncompensatedShift = deviationFromIsopotential * (slopeThermalFactor - 1.0) * THERMAL_SLOPE_SHIFT_COEFFICIENT;

        return nominalPh + uncompensatedShift;
    }

    /**
     * NATIVE PHYSICS: Continuous runtime under harsh process conditions degrades glass active sites.
     * High alkaline or acidic environments trigger chemical poisoning, leading to response compression.
     */
    private double applyElectrodeFoulingAndPoisoning(double level) {
        if (level > SENSITIVITY_DEGRADATION_THRESHOLD_PH) {
            double excess = level - SENSITIVITY_DEGRADATION_THRESHOLD_PH;
            // Analytical compression due to lithium-ion errors in high alkaline processing
            return SENSITIVITY_DEGRADATION_THRESHOLD_PH + (excess * (1.0 - FOULING_COMPRESSION_FACTOR));
        }
        return level;
    }

    private double generatePreampNoise(ThreadLocalRandom random) {
        // High glass membrane internal resistance ($100\text{ M}\Omega+$) triggers thermal Johnson noise amplification
        return random.nextGaussian() * (accuracy * 0.20);
    }

    private double generateStreamingCurrentNoise(double processTemperature, ThreadLocalRandom random) {
        ProcessArea area = getLocation().area();
        // Dynamic liquid velocities passing over the glass outer layer generate transient electrostatic flow charges
        double fluidTurbulenceFactor = switch (area) {
            case PIPELINE_SECTION -> 0.025;      // High flow friction noise profiles
            case REACTOR_SECTION -> 0.015;       // Intermittent agitator shear wave pulses
            case COOLING_SECTION -> 0.010;       // Large volume aeration pump turbulence
            default -> 0.003;                    // Calm process channels or static drainage
        };
        // Ion mobility and dynamic streaming noise scale linearly with thermal kinetic state
        double thermalActivityRatio = (processTemperature + ABSOLUTE_ZERO_KELVIN) / (CALIBRATION_REFERENCE_TEMP_C + ABSOLUTE_ZERO_KELVIN);
        return random.nextGaussian() * fluidTurbulenceFactor * thermalActivityRatio;
    }
}
