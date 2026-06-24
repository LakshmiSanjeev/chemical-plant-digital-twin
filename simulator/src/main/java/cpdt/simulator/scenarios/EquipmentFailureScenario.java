package cpdt.simulator.scenarios;

import cpdt.common.enums.DeviceStatus;
import cpdt.simulator.SensorDevice;
import cpdt.simulator.environment.PlantEnvironment;

import java.util.Objects;

public class EquipmentFailureScenario extends Scenario {

    private final SensorDevice targetSensor;
    private DeviceStatus originalStatus;

    private boolean isActivated = false;

    public EquipmentFailureScenario(String scenarioId, SensorDevice targetSensor, PlantEnvironment plantEnvironment) {
        super(scenarioId, "Equipment Failure Scenario", targetSensor.getLocation().area(), plantEnvironment);
        this.targetSensor = Objects.requireNonNull(targetSensor, "Target sensor cannot be null");
    }

    @Override
    public void activate() {
        if (isActivated) {
            return;
        }

        this.originalStatus = targetSensor.getStatus();

        targetSensor.setStatus(DeviceStatus.CRITICAL);
        targetSensor.setLastUpdated(System.currentTimeMillis());

        targetSensor.setCurrentValue(0.0);

        this.isActivated = true;
        System.out.printf("Equipment failure triggered: %s (%s) - Target sensor reading zeroed out locally.%n",
                targetSensor.getName(), targetSensor.getDeviceId());
    }

    @Override
    public void deactivate() {
        if (!isActivated) {
            return;
        }

        targetSensor.setStatus(originalStatus);
        targetSensor.setLastUpdated(System.currentTimeMillis());

        double currentEnvReading = plantEnvironment.getValue(getAffectedArea(), targetSensor.getMeasurementType());
        targetSensor.setCurrentValue(currentEnvReading);

        this.isActivated = false;
        System.out.printf("Equipment restored: %s (%s) - Target sensor metrics restored.%n",
                targetSensor.getName(), targetSensor.getDeviceId());
    }

    @Override
    public void update(long deltaTimeMs) {
        // Handled automatically by SimulatorEngine
    }

}
