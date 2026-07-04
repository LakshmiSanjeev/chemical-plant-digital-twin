package cpdt.simulator.scenarios;

import cpdt.common.enums.MeasurementType;
import cpdt.common.enums.ProcessArea;
import cpdt.simulator.environment.PlantEnvironment;

/**
 * Simulates a gradual hazardous gas leak within a process area.
 *
 * <p>This scenario progressively increases the gas concentration in the
 * simulated plant environment until a specified target concentration is
 * reached. Once deactivated, the gas concentration gradually returns to
 * its original operating level through the simulation update cycle.
 *
 * <p>This scenario provides realistic environmental changes for testing
 * gas monitoring, abnormal condition detection, and Digital Twin
 * response under leak conditions.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */

public class GasLeakScenario extends Scenario {

    private final double targetLeakConcentrationPpm;
    private final double leakRatePerMs;
    private double originalGasConcentration;
    private double currentConcentrationDelta;

    public GasLeakScenario(String scenarioId, ProcessArea affectedArea, PlantEnvironment plantEnvironment, double leakConcentrationPpm) {
        super(scenarioId, "Gas Leak Scenario", affectedArea, plantEnvironment);
        this.targetLeakConcentrationPpm = leakConcentrationPpm;
        this.leakRatePerMs = leakConcentrationPpm / 5000.0;
        this.currentConcentrationDelta = 0.0;
    }

    @Override
    public void activate() {
        originalGasConcentration = plantEnvironment.getValue(getAffectedArea(), MeasurementType.GAS_CONCENTRATION);
    }

    @Override
    public void deactivate() {
        // Handled dynamically via update loop
    }

    @Override
    public void update(long deltaTimeMs) {
        if (isActive()) {
            if (currentConcentrationDelta < targetLeakConcentrationPpm) {
                double increment = leakRatePerMs * deltaTimeMs;
                currentConcentrationDelta = Math.min(targetLeakConcentrationPpm, currentConcentrationDelta + increment);
                plantEnvironment.setValue(getAffectedArea(), MeasurementType.GAS_CONCENTRATION, originalGasConcentration + currentConcentrationDelta);
            }
        }
        else {
            if (currentConcentrationDelta > 0.0) {
                double decrement = leakRatePerMs * deltaTimeMs;
                currentConcentrationDelta = Math.max(0.0, currentConcentrationDelta - decrement);
                plantEnvironment.setValue(getAffectedArea(), MeasurementType.GAS_CONCENTRATION, originalGasConcentration + currentConcentrationDelta);
            }
        }
    }

    @Override
    public boolean isFullyResolved() {
        return !isActive() && currentConcentrationDelta <= 0.0;
    }
}
