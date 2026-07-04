package cpdt.simulator.utilities;

import cpdt.common.enums.DeviceType;
import cpdt.common.enums.MeasurementType;
import cpdt.common.models.Location;
import cpdt.simulator.SensorDevice;
import cpdt.simulator.environment.PlantEnvironment;

import java.util.concurrent.ThreadLocalRandom;

public class TestSensor extends SensorDevice {

    public TestSensor(String deviceId, String name, Location location, PlantEnvironment environment) {
        this(deviceId, name, location, MeasurementType.TEMPERATURE, environment);
    }

    public TestSensor(String deviceId, String name, Location location, MeasurementType measurementType, PlantEnvironment environment) {
        super(deviceId, name, DeviceType.TEMPERATURE_SENSOR, location, measurementType, environment);
        this.minRange = 0.0;
        this.maxRange = 100.0;
        this.driftVariancePerHour = 0.01;
        calculateHardwareResolution();
    }

    @Override
    public double getReading() {
        return getEnvironmentValue();
    }

    public double readEnvironmentValue() {
        return getEnvironmentValue();
    }

    public double quantize(double value) {
        return applyAdcQuantization(value);
    }

    public void updateDrift(ThreadLocalRandom random) {
        updateLongTermDrift(10.0, random);
    }

    public void calculateResolution() {
        calculateHardwareResolution();
    }
}