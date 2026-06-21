package cpdt.simulator.scenarios;

import cpdt.common.enums.ProcessArea;
import cpdt.simulator.environment.PlantEnvironment;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

public abstract class Scenario {

    @Getter
    private final String scenarioId;
    @Getter
    private final String name;
    @Getter
    private final ProcessArea affectedArea;
    protected final PlantEnvironment plantEnvironment;

    @Setter
    private boolean active;

    protected Scenario(String scenarioId, String name, ProcessArea affectedArea, PlantEnvironment plantEnvironment) {
        this.scenarioId = Objects.requireNonNull(scenarioId, "Scenario ID cannot be null");
        this.name = Objects.requireNonNull(name, "Scenario name cannot be null");
        this.affectedArea = Objects.requireNonNull(affectedArea, "Affected area cannot be null");
        this.plantEnvironment = Objects.requireNonNull(plantEnvironment, "PlantEnvironment cannot be null");
        this.active = false;
    }

    public abstract void activate();

    public abstract void deactivate();

    public abstract void update(long deltaTimeMs);

    public boolean isActive() {
        return active;
    }

    public boolean isFullyResolved() {
        return !isActive();
    }
}
