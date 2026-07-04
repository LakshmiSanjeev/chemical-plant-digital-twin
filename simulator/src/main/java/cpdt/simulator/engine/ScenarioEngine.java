package cpdt.simulator.engine;

import cpdt.simulator.environment.PlantEnvironment;
import cpdt.simulator.scenarios.Scenario;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the lifecycle and execution of all active simulation scenarios.
 * <p>
 * The ScenarioEngine is responsible for activating, updating, and clearing
 * scenarios that modify the plant environment. Each active scenario is
 * updated during every simulation tick until it has fully completed its
 * lifecycle.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
public class ScenarioEngine {

    @Getter
    private final PlantEnvironment plantEnvironment;

    private final Set<Scenario> trackedScenarios;

    public ScenarioEngine(PlantEnvironment plantEnvironment) {
        this.plantEnvironment = Objects.requireNonNull(plantEnvironment, "PlantEnvironment cannot be null");
        this.trackedScenarios = ConcurrentHashMap.newKeySet();
    }

    /**
     * Activates a scenario and begins tracking it for periodic updates.
     * If the scenario is already active, no action is taken.
     *
     * @param scenario the scenario to activate
     * @throws NullPointerException if the scenario is null
     */
    public void triggerScenario(Scenario scenario) {
        Objects.requireNonNull(scenario, "Scenario cannot be null");
        if (scenario.isActive()) {
            return;
        }
        scenario.activate();
        scenario.setActive(true);
        trackedScenarios.add(scenario);
        System.out.println("Scenario activated: " + scenario.getName());
    }
    /**
     * Deactivates an active scenario and allows it to complete any remaining
     * recovery or cooldown behavior before removal.
     *
     * @param scenario the scenario to deactivate
     * @throws NullPointerException if the scenario is null
     */
    public void clearScenario(Scenario scenario) {
        Objects.requireNonNull(scenario, "Scenario cannot be null");
        if (!scenario.isActive()) {
            return;
        }
        scenario.deactivate();
        scenario.setActive(false);
        System.out.println("Scenario cleared (Cooldown initiated): " + scenario.getName());
    }
    /**
     * Updates every tracked scenario using the specified simulation time step.
     * Scenarios that have fully resolved are removed from the engine.
     *
     * @param deltaTimeMs the elapsed simulation time in milliseconds since the previous update
     */
    public void update(long deltaTimeMs) {
        for (Scenario scenario : trackedScenarios) {
            try {
                scenario.update(deltaTimeMs);
                if (scenario.isFullyResolved()) {
                    trackedScenarios.remove(scenario);
                    System.out.println("Scenario fully resolved and purged from engine: " + scenario.getName());
                }
            }
            catch (Exception e) {
                System.err.println("Error executing update for scenario: " + scenario.getScenarioId());
            }
        }
    }
    /**
     * Returns all scenarios that are currently active.
     *
     * @return a list containing the active scenarios
     */
    public List<Scenario> getActiveScenarios() {
        return trackedScenarios.stream().filter(Scenario::isActive).toList();
    }
}
