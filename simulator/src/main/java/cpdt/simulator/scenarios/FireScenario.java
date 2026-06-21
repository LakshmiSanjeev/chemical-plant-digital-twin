package cpdt.simulator.scenarios;

import cpdt.common.enums.ProcessArea;
import cpdt.simulator.environment.PlantEnvironment;

public class FireScenario extends Scenario {

    private final OverheatScenario overheatScenario;
    private final GasLeakScenario gasLeakScenario;

    public FireScenario(String scenarioId, ProcessArea affectedArea, PlantEnvironment plantEnvironment) {
        super(scenarioId, "Fire Scenario", affectedArea, plantEnvironment);
        this.overheatScenario = new OverheatScenario(scenarioId + "_HEAT", affectedArea, plantEnvironment, 80.0);
        this.gasLeakScenario = new GasLeakScenario(scenarioId + "_GAS", affectedArea, plantEnvironment, 450.0);
    }

    @Override
    public void activate() {
        overheatScenario.activate();
        gasLeakScenario.activate();
        System.out.printf("Fire Scenario triggered in %s: Extreme heat and gas leak conditions applied.%n",
                getAffectedArea());
    }

    @Override
    public void deactivate() {
        overheatScenario.deactivate();
        gasLeakScenario.deactivate();
        System.out.printf("Fire Scenario cleared in %s: Environmental parameters returning to baseline.%n", getAffectedArea());
    }

    @Override
    public void update(long deltaTimeMs) {
        overheatScenario.update(deltaTimeMs);
        gasLeakScenario.update(deltaTimeMs);
    }
}
