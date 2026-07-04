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
 * Represents a simulated Differential Pressure (DP) Flow Meter
 * used within the chemical plant simulation.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
public class FlowSensor extends SensorDevice {

    private static final double DEFAULT_MIN_RANGE = 0.0;
    private static final double DEFAULT_MAX_RANGE = 150.0;
    private static final double BASE_ACCURACY_PERCENT_FS = 0.10;
    private static final double THERMAL_REFERENCE_TEMP_C = 25.0;
    private static final double FLUID_DENSITY_TEMP_COEFFICIENT = -0.0004;
    private static final double LOW_FLOW_CUTOFF_PERCENT = 0.03;

    private double smoothedFlowRate;
    private final double flowTimeConstantSeconds;
    private long lastReadingTimestamp;

    public FlowSensor(String deviceId, String name, Location location, PlantEnvironment plantEnvironment) {
        super(deviceId, name, DeviceType.FLOW_SENSOR, location, MeasurementType.FLOW_RATE, plantEnvironment);
        Objects.requireNonNull(location, "Location cannot be null");

        this.minRange = DEFAULT_MIN_RANGE;
        this.maxRange = DEFAULT_MAX_RANGE;

        calculateHardwareResolution();

        this.driftVariancePerHour = 0.0005 * 0.0005;

        this.lowAlarmLimit = 2.0;
        this.highAlarmLimit = 135.0;

        this.accuracy = (BASE_ACCURACY_PERCENT_FS / 100.0) * (maxRange - minRange);

        this.hysteresis = 0.5;

        this.samplingIntervalMs = 250;

        double initialEnvironmentFlow = plantEnvironment.getValue(location.area(), MeasurementType.FLOW_RATE);
        this.smoothedFlowRate = initialEnvironmentFlow;
        setCurrentValue(initialEnvironmentFlow);

        this.lastReadingTimestamp = System.currentTimeMillis();
        this.flowTimeConstantSeconds = determineFlowTimeConstant(location.area());
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

        double alpha = 1.0 - Math.exp(-deltaTimeSeconds / flowTimeConstantSeconds);
        smoothedFlowRate += alpha * (processFlow - smoothedFlowRate);

        double processTemperature = getPlantEnvironment().getValue(getLocation().area(), MeasurementType.TEMPERATURE);

        ThreadLocalRandom random = ThreadLocalRandom.current();

        double measuredFlow = applyFluidDensityCorrection(smoothedFlowRate, processTemperature);
        measuredFlow = applyLowFlowCutoff(measuredFlow);

        double dpCellElectronicNoise = generateElectronicNoise(random);
        double turbulentFluctuation = generateTurbulentFluctuation(measuredFlow, random);
        updateLongTermDrift(deltaTimeSeconds, random);
        double physicalFlow = measuredFlow + dpCellElectronicNoise + turbulentFluctuation + accumulatedLongTermDrift;

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
            case UTILITIES_SECTION, COOLING_SECTION -> 0.5;
            case STORAGE_SECTION -> 1.2;
        };
    }

    private double applyFluidDensityCorrection(double nominalFlow, double currentTemperature) {
        double temperatureDeviation = currentTemperature - THERMAL_REFERENCE_TEMP_C;
        double densityRatio = 1.0 + (temperatureDeviation * FLUID_DENSITY_TEMP_COEFFICIENT);

        densityRatio = Math.max(densityRatio, 0.5);

        return nominalFlow / Math.sqrt(densityRatio);
    }

    private double applyLowFlowCutoff(double flow) {
        double cutoffThreshold = maxRange * LOW_FLOW_CUTOFF_PERCENT;
        if (flow < cutoffThreshold) {
            return 0.0;
        }
        return flow;
    }

    private double generateElectronicNoise(ThreadLocalRandom random) {
        return random.nextGaussian() * (accuracy * 0.15);
    }

    private double generateTurbulentFluctuation(double currentFlow, ThreadLocalRandom random) {
        ProcessArea area = getLocation().area();
        double turbulenceIntensity = switch (area) {
            case PIPELINE_SECTION -> 0.035;
            case REACTOR_SECTION -> 0.025;
            case DISTILLATION_SECTION -> 0.020;
            case FEED_SECTION -> 0.015;
            default -> 0.010;
        };
        return random.nextGaussian() * currentFlow * turbulenceIntensity;
    }
}
