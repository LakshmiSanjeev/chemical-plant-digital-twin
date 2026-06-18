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
 * Industrial-grade Differential Pressure (DP) Orifice Flow Sensor Simulation.
 * Fully unified architecture matching the Pressure and Temperature designs,
 * while preserving native fluid dynamics and square-root flow relationships.
 */
public class FlowSensor extends SensorDevice {

    // physical operating limits of the sensor hardware (m³/h)
    private static final double DEFAULT_MIN_RANGE = 0.0;
    private static final double DEFAULT_MAX_RANGE = 150.0;

    // Smart DP transmitter accuracy base specification (Percentage of Full Scale)
    private static final double BASE_ACCURACY_PERCENT_FS = 0.10;

    // Fluid density variation approximations based on local temperature deviations
    private static final double THERMAL_REFERENCE_TEMP_C = 25.0;
    private static final double FLUID_DENSITY_TEMP_COEFFICIENT = -0.0004; // Density decrease per degree C


    // Low-flow cut-off threshold (typically 2-5% of max range) to avoid square-root noise explosion
    private static final double LOW_FLOW_CUTOFF_PERCENT = 0.03;

    // NATIVE PHYSICS PRESERVED: Drift variance matched to DP cell stability profiles
    private static final double DRIFT_VARIANCE_PER_HOUR = 0.0005 * 0.0005;

    // internal hydrodynamic flow state
    private double smoothedFlowRate;
    // hydrodynamic lag / pipe flow development time constant
    private final double flowTimeConstantSeconds;
    // previous measurement timestamp
    private long lastReadingTimestamp;

    // UNIFORMITY CORRECTION: Stateful continuous Random Walk tracking
    private double accumulatedLongTermDrift = 0.0;

    public FlowSensor(String deviceId, String name, Location location, PlantEnvironment plantEnvironment) {
        super(deviceId, name, DeviceType.FLOW_SENSOR, location, MeasurementType.FLOW_RATE, plantEnvironment);
        Objects.requireNonNull(location, "Location cannot be null");
        this.minRange = DEFAULT_MIN_RANGE;
        this.maxRange = DEFAULT_MAX_RANGE;

        // UNIFORMITY CORRECTION: Native 16-bit physical hardware LSB step size
        this.resolution = (maxRange - minRange) / (Math.pow(2, ADC_RESOLUTION_BITS) - 1);

        this.lowAlarmLimit = 2.0;
        this.highAlarmLimit = 135.0;
        this.accuracy = (BASE_ACCURACY_PERCENT_FS / 100.0) * (maxRange - minRange);
        this.hysteresis = 0.5;
        this.samplingIntervalMs = 250; // Flow loops usually run faster than pressure/temperature

        double initialEnvironmentFlow = plantEnvironment.getValue(location.area(), MeasurementType.FLOW_RATE);
        this.smoothedFlowRate = initialEnvironmentFlow;
        this.lastReadingTimestamp = System.currentTimeMillis();
        this.flowTimeConstantSeconds = determineFlowTimeConstant(location.area());
        setCurrentValue(initialEnvironmentFlow);
    }

    @Override
    public double getReading() {
        long currentTimestamp = System.currentTimeMillis();
        double deltaTimeSeconds = (currentTimestamp - lastReadingTimestamp) / 1000.0;
        if (deltaTimeSeconds <= 0.0) {
            return getCurrentValue();
        }
        lastReadingTimestamp = currentTimestamp;
        double processFlow = getEnvironmentValue();

        // Exponential Moving Average (EMA) filter representing flow development delay
        double alpha = 1.0 - Math.exp(-deltaTimeSeconds / flowTimeConstantSeconds);
        smoothedFlowRate += alpha * (processFlow - smoothedFlowRate);

        // Fetch physical coupled variables for fluid density calculations
        double processTemperature = getPlantEnvironment().getValue(getLocation().area(), MeasurementType.TEMPERATURE);

        ThreadLocalRandom random = ThreadLocalRandom.current();

        // Native Hydrodynamic Orifice Physics Math Model
        double measuredFlow = applyFluidDensityCorrection(smoothedFlowRate, processTemperature);
        measuredFlow = applyLowFlowCutoff(measuredFlow);

        // Native noise profiles preserved
        double dpCellElectronicNoise = generateElectronicNoise(random);
        double turbulentFluctuation = generateTurbulentFluctuation(measuredFlow, random);

        // UNIFORMITY CORRECTION: Stateful drift step execution
        updateLongTermDrift(deltaTimeSeconds, random);

        double physicalFlow = measuredFlow + dpCellElectronicNoise + turbulentFluctuation + accumulatedLongTermDrift;

        // UNIFORMITY CORRECTION: Single-layer physical ADC quantization tracking with hard limits
        double digitizedFlow = applyAdcQuantization(physicalFlow);
        digitizedFlow = Math.clamp(digitizedFlow, minRange, maxRange);

        setCurrentValue(digitizedFlow);
        return digitizedFlow;
    }

    private double determineFlowTimeConstant(ProcessArea area) {
        return switch (area) {
            case REACTOR_SECTION -> 0.8;
            case PIPELINE_SECTION -> 0.4;
            case FEED_SECTION -> 0.3;
            case DISTILLATION_SECTION -> 0.6;
            case UTILITIES_SECTION -> 0.5;
            case STORAGE_SECTION -> 1.2;
            case COOLING_SECTION -> 0.5;
        };
    }

    /**
     * NATIVE PHYSICS: Volumetric flow through an orifice varies inversely with the square root of fluid density.
     * Changes in process temperature deviate the density from calibration values.
     */
    private double applyFluidDensityCorrection(double nominalFlow, double currentTemperature) {
        double temperatureDeviation = currentTemperature - THERMAL_REFERENCE_TEMP_C;
        double densityRatio = 1.0 + (temperatureDeviation * FLUID_DENSITY_TEMP_COEFFICIENT);

        // Ensure a physical lower bound to prevent imaginary numbers in square root calculations
        densityRatio = Math.max(densityRatio, 0.5);

        // Flow rate behaves inversely proportional to sqrt of density ratio change
        return nominalFlow / Math.sqrt(densityRatio);
    }

    /**
     * NATIVE PHYSICS: At low flows near zero, differential pressure approaches zero rapidly ($DP \propto Q^2$).
     * Standard square-root extraction transmitters utilize a low-flow cutoff to eliminate noisy erratic readings.
     */
    private double applyLowFlowCutoff(double flow) {
        double cutoffThreshold = maxRange * LOW_FLOW_CUTOFF_PERCENT;
        if (flow < cutoffThreshold) {
            return 0.0;
        }
        return flow;
    }

    private double generateElectronicNoise(ThreadLocalRandom random) {
        // High-frequency DP cell noise amplifier floor
        return random.nextGaussian() * (accuracy * 0.15);
    }

    private double generateTurbulentFluctuation(double currentFlow, ThreadLocalRandom random) {
        ProcessArea area = getLocation().area();
        // Reynolds number/Turbulence varies significantly per chemical plant processing section
        double turbulenceIntensity = switch (area) {
            case PIPELINE_SECTION -> 0.035;
            case REACTOR_SECTION -> 0.025;
            case DISTILLATION_SECTION -> 0.020;
            case FEED_SECTION -> 0.015;
            default -> 0.010;
        };
        // Turbulent noise intensity scales dynamically with the velocity profile (flow rate)
        return random.nextGaussian() * currentFlow * turbulenceIntensity;
    }

    // UNIFORMITY CORRECTION: Incremental random walk calculation matching pressure class architecture
    private void updateLongTermDrift(double deltaTimeSeconds, ThreadLocalRandom random) {
        double deltaTimeHours = deltaTimeSeconds / 3600.0;
        double stepStandardDeviation = Math.sqrt(deltaTimeHours * DRIFT_VARIANCE_PER_HOUR);
        accumulatedLongTermDrift += random.nextGaussian() * stepStandardDeviation;
    }

    // UNIFORMITY CORRECTION: Straight mathematical conversion mapping direct to resolution constraints
}
