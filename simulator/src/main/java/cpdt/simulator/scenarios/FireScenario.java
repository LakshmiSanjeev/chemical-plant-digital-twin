package cpdt.simulator.scenarios;

import cpdt.common.enums.ProcessArea;
import cpdt.simulator.environment.PlantEnvironment;

/**
 * Simulates a fire emergency within a process area.
 *
 * <p>This scenario represents a composite hazardous event by combining
 * an overheat scenario and a gas leak scenario affecting the same
 * process area. It coordinates the activation, update, and resolution
 * of both underlying scenarios to model realistic fire conditions.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */

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
        overheatScenario.setActive(true);
        gasLeakScenario.activate();
        gasLeakScenario.setActive(true);
        setActive(true);
        System.out.printf("Fire Scenario triggered in %s: Extreme heat and gas leak conditions applied.%n", getAffectedArea());
    }

    @Override
    public void deactivate() {
        overheatScenario.deactivate();
        overheatScenario.setActive(false);
        gasLeakScenario.deactivate();
        gasLeakScenario.setActive(false);
        setActive(false);
        System.out.printf("Fire Scenario cleared in %s: Environmental parameters returning to baseline.%n", getAffectedArea());

    }

    @Override
    public void update(long deltaTimeMs) {
        overheatScenario.update(deltaTimeMs);
        gasLeakScenario.update(deltaTimeMs);
    }

    @Override
    public boolean isFullyResolved() {
        return !isActive() && overheatScenario.isFullyResolved() && gasLeakScenario.isFullyResolved();
    }
}
