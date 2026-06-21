package cpdt.simulator.scenarios;

import cpdt.common.enums.DeviceStatus;
import cpdt.common.enums.MeasurementType;
import cpdt.common.models.Device;
import cpdt.simulator.environment.PlantEnvironment;

import java.util.Objects;

public class EquipmentFailureScenario extends Scenario {

    private final Device targetDevice;
    private DeviceStatus originalStatus;
    private double originalSensorValue;
    private MeasurementType primaryMeasurementType;

    public EquipmentFailureScenario(String scenarioId, Device targetDevice, PlantEnvironment plantEnvironment) {
        super(scenarioId, "Equipment Failure Scenario", targetDevice.getLocation().area(), plantEnvironment);
        this.targetDevice = Objects.requireNonNull(targetDevice, "Target device cannot be null");
        this.primaryMeasurementType = matchMeasurementTypeByDevice(targetDevice);
    }

    @Override
    public void activate() {
        originalStatus = targetDevice.getStatus();
        targetDevice.setStatus(DeviceStatus.CRITICAL);
        targetDevice.setLastUpdated(System.currentTimeMillis());
        if (primaryMeasurementType != null) {
            originalSensorValue = plantEnvironment.getValue(getAffectedArea(), primaryMeasurementType);
            plantEnvironment.setValue(getAffectedArea(), primaryMeasurementType, 0.0);
        }
        System.out.printf("Equipment failure triggered: %s (%s) - Sensor reading zeroed out.%n", targetDevice.getName(), targetDevice.getDeviceId());
    }

    @Override
    public void deactivate() {
        targetDevice.setStatus(originalStatus);
        targetDevice.setLastUpdated(System.currentTimeMillis());
        if (primaryMeasurementType != null) {
            plantEnvironment.setValue(getAffectedArea(), primaryMeasurementType, originalSensorValue);
        }
        System.out.printf("Equipment restored: %s (%s) - Sensor metrics restored.%n", targetDevice.getName(), targetDevice.getDeviceId());
    }

    @Override
    public void update(long deltaTimeMs) {
        // Static scenario.
        // Reading remains zeroed out until cleared.
    }

    private MeasurementType matchMeasurementTypeByDevice(Device device) {
        if (device.getType() == null) {
            return null;
        }
        String typeName = device.getType().name().toUpperCase();
        if (typeName.contains("TEMPERATURE")) {
            return MeasurementType.TEMPERATURE;
        }
        else if (typeName.contains("PRESSURE")) {
            return MeasurementType.PRESSURE;
        }
        else if (typeName.contains("GAS")) {
            return MeasurementType.GAS_CONCENTRATION;
        }
        else if (typeName.contains("FLOW")) {
            return MeasurementType.FLOW_RATE;
        }
        else if (typeName.contains("LEVEL")) {
            return MeasurementType.LEVEL;
        }
        else if (typeName.contains("PH")) {
            return MeasurementType.PH;
        }
        return null;
    }
}
