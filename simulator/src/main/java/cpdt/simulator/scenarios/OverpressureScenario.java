package cpdt.simulator.scenarios;

import cpdt.common.enums.MeasurementType;
import cpdt.common.enums.ProcessArea;
import cpdt.simulator.environment.PlantEnvironment;

/**
 * Simulates a gradual pressure buildup within a process area.
 *
 * <p>This scenario progressively increases the environmental pressure
 * until a specified target pressure is reached. After deactivation,
 * the pressure gradually returns to its normal operating value through
 * the simulation update cycle.
 *
 * <p>This scenario provides realistic pressure variations for testing
 * abnormal process conditions and evaluating Digital Twin monitoring
 * capabilities.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */

public class OverpressureScenario extends Scenario {

    private final double targetPressureIncrease;
    private final double pressureRatePerMs;
    private double originalPressureBase;
    private double currentPressureDelta;

    public OverpressureScenario(String scenarioId, ProcessArea affectedArea, PlantEnvironment plantEnvironment, double pressureIncrease) {
        super(scenarioId, "Overpressure Scenario", affectedArea, plantEnvironment);
        this.targetPressureIncrease = pressureIncrease;
        this.pressureRatePerMs = pressureIncrease / 8000.0;
        this.currentPressureDelta = 0.0;
    }

    @Override
    public void activate() {
        originalPressureBase = plantEnvironment.getValue(getAffectedArea(), MeasurementType.PRESSURE);
    }

    @Override
    public void deactivate() {
        // simulation loop will gradually lower values back to baseline
    }

    @Override
    public void update(long deltaTimeMs) {
        if (isActive()) {
            if (currentPressureDelta < targetPressureIncrease) {
                double increment = pressureRatePerMs * deltaTimeMs;
                currentPressureDelta = Math.min(targetPressureIncrease, currentPressureDelta + increment);
                plantEnvironment.setValue(getAffectedArea(), MeasurementType.PRESSURE, originalPressureBase + currentPressureDelta);
            }
        }
        else {
            if (currentPressureDelta > 0.0) {
                double decrement = pressureRatePerMs * deltaTimeMs;
                currentPressureDelta = Math.max(0.0, currentPressureDelta - decrement);
                plantEnvironment.setValue(getAffectedArea(), MeasurementType.PRESSURE, originalPressureBase + currentPressureDelta);
            }
        }
    }

    @Override
    public boolean isFullyResolved() {
        return !isActive() && currentPressureDelta <= 0.0;
    }
}
