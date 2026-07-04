package cpdt.simulator.utilities;

import cpdt.common.enums.ProcessArea;
import cpdt.simulator.environment.PlantEnvironment;
import cpdt.simulator.scenarios.Scenario;

public class TestScenario extends Scenario {

    public boolean activateCalled;
    public boolean deactivateCalled;
    public boolean updateCalled;

    public int activateCount = 0;

    public boolean resolved = false;

    public TestScenario(
            String id,
            ProcessArea area,
            PlantEnvironment environment
    ) {
        super(id, "Test Scenario", area, environment);
    }

    @Override
    public void activate() {
        activateCalled = true;
        activateCount++;
    }

    @Override
    public void deactivate() {
        deactivateCalled = true;
    }

    @Override
    public void update(long deltaTimeMs) {
        updateCalled = true;
    }

    @Override
    public boolean isFullyResolved() {
        return resolved;
    }
}