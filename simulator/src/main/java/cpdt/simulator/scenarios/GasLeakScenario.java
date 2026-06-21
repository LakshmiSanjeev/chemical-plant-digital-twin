package cpdt.simulator.scenarios;

import cpdt.common.enums.MeasurementType;
import cpdt.common.enums.ProcessArea;
import cpdt.simulator.environment.PlantEnvironment;

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
