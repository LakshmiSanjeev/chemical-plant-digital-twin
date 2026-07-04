package cpdt.simulator.scenarios;

import cpdt.common.enums.ProcessArea;
import cpdt.simulator.environment.PlantEnvironment;

import lombok.Getter;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base abstraction for all simulation scenarios.
 *
 * <p>Provides the common state and lifecycle operations shared by all
 * simulation scenarios, including scenario identification, affected
 * process area, activation state, and access to the plant environment.
 * Concrete implementations define how specific process conditions are
 * introduced, updated, and resolved within the simulation.
 *
 * <p>This class follows the Template Method design pattern by defining
 * a common scenario lifecycle while allowing subclasses to implement
 * specialized simulation behavior.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */

public abstract class Scenario {

    @Getter
    private final String scenarioId;
    @Getter
    private final String name;
    @Getter
    private final ProcessArea affectedArea;
    protected final PlantEnvironment plantEnvironment;

    private final AtomicBoolean active = new AtomicBoolean(false);

    /**
     * Constructs a simulation scenario.
     *
     * <p>Initializes the common metadata and shared plant environment
     * required for a simulation scenario.
     *
     * @param scenarioId unique identifier for the scenario
     * @param name human-readable name of the scenario
     * @param affectedArea process area affected by the scenario
     * @param plantEnvironment simulated plant environment to be modified
     * @throws NullPointerException if any argument is {@code null}
     */
    protected Scenario(String scenarioId, String name, ProcessArea affectedArea, PlantEnvironment plantEnvironment) {
        this.scenarioId = Objects.requireNonNull(scenarioId, "Scenario ID cannot be null");
        this.name = Objects.requireNonNull(name, "Scenario name cannot be null");
        this.affectedArea = Objects.requireNonNull(affectedArea, "Affected area cannot be null");
        this.plantEnvironment = Objects.requireNonNull(plantEnvironment, "PlantEnvironment cannot be null");
    }
    /**
     * Activates the scenario.
     *
     * <p>Implementations should apply the initial environmental or device
     * changes required to begin the simulation event.
     */
    public abstract void activate();
    /**
     * Deactivates the scenario.
     *
     * <p>Implementations should initiate restoration of the affected
     * environment or device state back toward normal operating conditions.
     */
    public abstract void deactivate();
    /**
     * Updates the scenario state.
     *
     * <p>Called periodically by the simulation engine to advance the
     * scenario over time and apply incremental changes to the simulated
     * environment.
     *
     * @param deltaTimeMs elapsed time since the previous update, in milliseconds
     */
    public abstract void update(long deltaTimeMs);
    /**
     * Sets the activation state of the scenario.
     *
     * @param active {@code true} to activate the scenario state;
     *               {@code false} otherwise
     */
    public void setActive(boolean active) {
        this.active.set(active);
    }
    /**
     * Indication for the activation state of the scenario.
     */
    public boolean isActive() {
        return this.active.get();
    }
    /**
     * Indicates whether the scenario has completely returned to its
     * normal operating state.
     *
     * @return {@code true} if the scenario has been fully resolved;
     *         {@code false} otherwise
     */
    public boolean isFullyResolved() {
        return !isActive();
    }
}
