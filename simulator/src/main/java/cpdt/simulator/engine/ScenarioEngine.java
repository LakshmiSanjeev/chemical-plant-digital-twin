package cpdt.simulator.engine;

import cpdt.simulator.environment.PlantEnvironment;
import cpdt.simulator.scenarios.Scenario;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ScenarioEngine {

    @Getter
    private final PlantEnvironment plantEnvironment;

    private final Set<Scenario> trackedScenarios;

    public ScenarioEngine(PlantEnvironment plantEnvironment) {
        this.plantEnvironment = Objects.requireNonNull(plantEnvironment, "PlantEnvironment cannot be null");
        this.trackedScenarios = ConcurrentHashMap.newKeySet();
    }

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

    public void clearScenario(Scenario scenario) {
        Objects.requireNonNull(scenario, "Scenario cannot be null");
        if (!scenario.isActive()) {
            return;
        }
        scenario.deactivate();
        scenario.setActive(false);
        System.out.println("Scenario cleared (Cooldown initiated): " + scenario.getName());
    }

    public void update(long deltaTimeMs) {
        for (Scenario scenario : trackedScenarios) {
            try {
                scenario.update(deltaTimeMs);
                if (scenario.isFullyResolved()) {
                    trackedScenarios.remove(scenario);
                    System.out.println("Scenario fully resolved and purged from engine: " + scenario.getName());
                }
            } catch (Exception e) {
                System.err.println("Error executing update for scenario: " + scenario.getScenarioId());
                e.printStackTrace();
            }
        }
    }

    public List<Scenario> getActiveScenarios() {
        return trackedScenarios.stream().filter(Scenario::isActive).toList();
    }
}
