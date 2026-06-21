package cpdt.simulator.engine;

import cpdt.simulator.environment.PlantEnvironment;
import cpdt.simulator.scenarios.Scenario;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class ScenarioEngine {

    @Getter
    private final PlantEnvironment plantEnvironment;
    private final List<Scenario> trackedScenarios;

    public ScenarioEngine(PlantEnvironment plantEnvironment) {
        this.plantEnvironment = Objects.requireNonNull(plantEnvironment, "PlantEnvironment cannot be null");
        this.trackedScenarios = new CopyOnWriteArrayList<>();
    }

    public void triggerScenario(Scenario scenario) {
        Objects.requireNonNull(scenario, "Scenario cannot be null");
        if (scenario.isActive()) {
            return;
        }
        scenario.activate();
        scenario.setActive(true);

        if (!trackedScenarios.contains(scenario)) {
            trackedScenarios.add(scenario);
        }
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
            scenario.update(deltaTimeMs);
            if (scenario.isFullyResolved()) {
                trackedScenarios.remove(scenario);
                System.out.println("Scenario fully resolved and purged from engine: " + scenario.getName());
            }
        }
    }

    public List<Scenario> getActiveScenarios() {
        return trackedScenarios.stream().filter(Scenario::isActive).toList();
    }
}
