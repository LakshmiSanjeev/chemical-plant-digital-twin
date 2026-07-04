package cpdt.simulator.scenarios;

import cpdt.common.enums.MeasurementType;
import cpdt.common.enums.ProcessArea;
import cpdt.simulator.environment.PlantEnvironment;

/**
 * Simulates a gradual temperature increase within a process area.
 *
 * <p>This scenario progressively raises the environmental temperature
 * until a specified target increase is reached. When the scenario is
 * cleared, the temperature gradually returns to its baseline value
 * through successive simulation updates.
 *
 * <p>This scenario is used to model abnormal thermal operating
 * conditions and evaluate system behavior during overheating events.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */

public class OverheatScenario extends Scenario {

    private final double targetTemperatureIncrease;
    private final double heatRatePerMs;
    private double originalBaseTemperature;
    private double currentAddedTemperature;

    public OverheatScenario(String scenarioId, ProcessArea affectedArea, PlantEnvironment plantEnvironment, double temperatureIncrease) {
        super(scenarioId, "Overheat Scenario", affectedArea, plantEnvironment);
        this.targetTemperatureIncrease = temperatureIncrease;
        this.heatRatePerMs = temperatureIncrease / 10000.0;
        this.currentAddedTemperature = 0.0;
    }

    @Override
    public void activate() {
        originalBaseTemperature = plantEnvironment.getValue(getAffectedArea(), MeasurementType.TEMPERATURE);
    }

    @Override
    public void deactivate() {
        // simulation loop will gradually lower values back to baseline
    }

    @Override
    public void update(long deltaTimeMs) {
        if (isActive()) {
            if (currentAddedTemperature < targetTemperatureIncrease) {
                double increment = heatRatePerMs * deltaTimeMs;
                currentAddedTemperature = Math.min(targetTemperatureIncrease, currentAddedTemperature + increment);
                plantEnvironment.setValue(getAffectedArea(), MeasurementType.TEMPERATURE, originalBaseTemperature + currentAddedTemperature);
            }
        }
        else {
            if (currentAddedTemperature > 0.0) {
                double decrement = heatRatePerMs * deltaTimeMs;
                currentAddedTemperature = Math.max(0.0, currentAddedTemperature - decrement);
                plantEnvironment.setValue(getAffectedArea(), MeasurementType.TEMPERATURE, originalBaseTemperature + currentAddedTemperature);
            }
        }
    }

    @Override
    public boolean isFullyResolved() {
        return !isActive() && currentAddedTemperature <= 0.0;
    }
}
