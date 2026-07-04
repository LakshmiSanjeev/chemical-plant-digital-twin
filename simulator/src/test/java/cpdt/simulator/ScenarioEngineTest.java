package cpdt.simulator;

import cpdt.common.enums.ProcessArea;
import cpdt.simulator.engine.ScenarioEngine;
import cpdt.simulator.environment.PlantEnvironment;
import cpdt.simulator.utilities.TestScenario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScenarioEngineTest {

    private PlantEnvironment plantEnvironment;
    private ScenarioEngine scenarioEngine;

    @BeforeEach
    void setUp() {
        plantEnvironment = new PlantEnvironment();
        scenarioEngine = new ScenarioEngine(plantEnvironment);
    }

    @Test
    void constructorShouldRejectNullPlantEnvironment() {
        assertThrows(NullPointerException.class, () -> new ScenarioEngine(null));
    }

    @Test
    void triggerScenarioShouldActivateAndTrackScenario() {
        TestScenario scenario = new TestScenario("TEST-1", ProcessArea.REACTOR_SECTION, plantEnvironment);
        scenarioEngine.triggerScenario(scenario);
        assertTrue(scenario.isActive());
        assertEquals(1, scenarioEngine.getActiveScenarios().size());
        assertTrue(scenarioEngine.getActiveScenarios().contains(scenario));
        assertTrue(scenario.activateCalled);
    }

    @Test
    void triggerScenarioShouldIgnoreAlreadyActiveScenario() {
        TestScenario scenario = new TestScenario("TEST-1", ProcessArea.REACTOR_SECTION, plantEnvironment);
        scenarioEngine.triggerScenario(scenario);
        scenarioEngine.triggerScenario(scenario);
        assertEquals(1, scenarioEngine.getActiveScenarios().size());
        assertEquals(1, scenario.activateCount);
    }

    @Test
    void clearScenarioShouldDeactivateScenario() {
        TestScenario scenario = new TestScenario("TEST-1", ProcessArea.REACTOR_SECTION, plantEnvironment);
        scenarioEngine.triggerScenario(scenario);
        scenarioEngine.clearScenario(scenario);
        assertFalse(scenario.isActive());
        assertTrue(scenario.deactivateCalled);
        assertTrue(scenarioEngine.getActiveScenarios().isEmpty());
    }

    @Test
    void updateShouldRemoveFullyResolvedScenario() {
        TestScenario scenario = new TestScenario("TEST-1", ProcessArea.REACTOR_SECTION, plantEnvironment);
        scenarioEngine.triggerScenario(scenario);
        scenarioEngine.clearScenario(scenario);
        scenario.resolved = true;
        scenarioEngine.update(100);
        assertTrue(scenario.updateCalled);
        assertTrue(scenarioEngine.getActiveScenarios().isEmpty());
    }
}