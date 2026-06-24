package cpdt.simulator.scenarios;

import cpdt.common.enums.ProcessArea;
import cpdt.simulator.environment.PlantEnvironment;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Scenario {

    @Getter
    private final String scenarioId;
    @Getter
    private final String name;
    @Getter
    private final ProcessArea affectedArea;
    protected final PlantEnvironment plantEnvironment;

    private final AtomicBoolean active = new AtomicBoolean(false);

    protected Scenario(String scenarioId, String name, ProcessArea affectedArea, PlantEnvironment plantEnvironment) {
        this.scenarioId = Objects.requireNonNull(scenarioId, "Scenario ID cannot be null");
        this.name = Objects.requireNonNull(name, "Scenario name cannot be null");
        this.affectedArea = Objects.requireNonNull(affectedArea, "Affected area cannot be null");
        this.plantEnvironment = Objects.requireNonNull(plantEnvironment, "PlantEnvironment cannot be null");
    }

    public abstract void activate();

    public abstract void deactivate();

    public abstract void update(long deltaTimeMs);

    public void setActive(boolean active) {
        this.active.set(active);
    }

    public boolean isActive() {
        return this.active.get();
    }

    public boolean isFullyResolved() {
        return !isActive();
    }
}
