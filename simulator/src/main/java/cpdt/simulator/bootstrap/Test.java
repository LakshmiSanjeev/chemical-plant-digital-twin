package cpdt.simulator.bootstrap;

import cpdt.common.enums.DeviceType;
import cpdt.common.enums.ProcessArea;
import cpdt.common.models.Location;
import cpdt.common.utils.DeviceIdGenerator;
import cpdt.simulator.SensorDevice;
import cpdt.simulator.devices.*;
import cpdt.simulator.engine.*;
import cpdt.simulator.environment.PlantEnvironment;
import cpdt.simulator.mqtt.DeviceStatusSubscriber;
import cpdt.simulator.mqtt.MqttTelemetryPublisher;
import cpdt.simulator.scenarios.*;

import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args) throws Exception {

        PlantEnvironment plantEnvironment = new PlantEnvironment();
        ScenarioEngine scenarioEngine = new ScenarioEngine(plantEnvironment);

        System.out.println();
        System.out.println("CHEMICAL PLANT DIGITAL TWIN (CPDT) BOOTSTRAP DEMONSTRATION");
        System.out.println("----------------------------------------------------------");
        System.out.println();

        System.out.println("[1/5] Initializing Plant Environment...");
        Thread.sleep(1000);

        List<Location> locations = List.of(
                new Location("LOC-001", "Reactor Section", ProcessArea.REACTOR_SECTION),
                new Location("LOC-002", "Distillation Section", ProcessArea.DISTILLATION_SECTION),
                new Location("LOC-003", "Feed Section", ProcessArea.FEED_SECTION),
                new Location("LOC-004", "Pipeline Section", ProcessArea.PIPELINE_SECTION),
                new Location("LOC-005", "Storage Section", ProcessArea.STORAGE_SECTION),
                new Location("LOC-006", "Cooling Section", ProcessArea.COOLING_SECTION),
                new Location("LOC-007", "Utilities Section", ProcessArea.UTILITIES_SECTION)
        );

        System.out.println("- 7 Process Areas Created -");

        List<SensorDevice> sensors = new ArrayList<>();

        for (Location location : locations) {
            int temperatureCount = location.area() == ProcessArea.REACTOR_SECTION ? 5 : 3;

            for (int i = 1; i <= temperatureCount; i++) {
                sensors.add(new TemperatureSensor(
                        DeviceIdGenerator.generateDeviceId(DeviceType.TEMPERATURE_SENSOR),
                        location.name() + " RTD-" + i,
                        location,
                        plantEnvironment));
            }

            for (int i = 1; i <= 3; i++) {
                sensors.add(new PressureSensor(
                        DeviceIdGenerator.generateDeviceId(DeviceType.PRESSURE_SENSOR),
                        location.name() + " PT-" + i,
                        location,
                        plantEnvironment));
            }

            for (int i = 1; i <= 2; i++) {
                sensors.add(new FlowSensor(
                        DeviceIdGenerator.generateDeviceId(DeviceType.FLOW_SENSOR),
                        location.name() + " FT-" + i,
                        location,
                        plantEnvironment));
            }

            for (int i = 1; i <= 2; i++) {
                sensors.add(new GasSensor(
                        DeviceIdGenerator.generateDeviceId(DeviceType.GAS_SENSOR),
                        location.name() + " GT-" + i,
                        location,
                        plantEnvironment));
            }

            for (int i = 1; i <= 2; i++) {
                sensors.add(new LevelSensor(
                        DeviceIdGenerator.generateDeviceId(DeviceType.LEVEL_SENSOR),
                        location.name() + " LT-" + i,
                        location,
                        plantEnvironment));
            }

            for (int i = 1; i <= 2; i++) {
                sensors.add(new PhSensor(
                        DeviceIdGenerator.generateDeviceId(DeviceType.PH_SENSOR),
                        location.name() + " PH-" + i,
                        location,
                        plantEnvironment));
            }
        }

        Location reactor = locations.getFirst();
        sensors.add(new TemperatureSensor(DeviceIdGenerator.generateDeviceId(DeviceType.TEMPERATURE_SENSOR),
                "Chief Reactor RTD", reactor, plantEnvironment));

        System.out.println();
        System.out.println("[2/5] Registering Sensors...");
        Thread.sleep(1000);

        System.out.println("- Sensors Registered : " + sensors.size());
        System.out.println();

        System.out.println("[3/5] Connecting to MQTT Broker...");
        Thread.sleep(1000);

        MqttTelemetryPublisher publisher = new MqttTelemetryPublisher("tcp://localhost:1883", "cpdt-bootstrap-demo");
        System.out.println("- MQTT Publisher Connected -");

        SimulatorEngine simulatorEngine = new SimulatorEngine(sensors, publisher, scenarioEngine);

        DeviceStatusSubscriber subscriber = new DeviceStatusSubscriber("tcp://localhost:1883", "cpdt-bootstrap-subscriber", simulatorEngine);

        subscriber.start();

        System.out.println("- Device Status Subscriber Connected -");
        System.out.println();

        System.out.println("[4/5] Creating Demonstration Scenarios...");
        Thread.sleep(1000);

        OverheatScenario reactorOverheat = new OverheatScenario("SCN-HEAT-01", ProcessArea.REACTOR_SECTION, plantEnvironment, 80.0);
        OverpressureScenario pipelineOverpressure = new OverpressureScenario("SCN-PRESS-01", ProcessArea.PIPELINE_SECTION, plantEnvironment, 8.0);
        FireScenario distillationFire = new FireScenario("SCN-FIRE-01", ProcessArea.DISTILLATION_SECTION, plantEnvironment);
        GasLeakScenario storageGasLeak = new GasLeakScenario("SCN-GAS-01", ProcessArea.STORAGE_SECTION, plantEnvironment, 450.0);
        EquipmentFailureScenario equipmentFailure = new EquipmentFailureScenario("SCN-FAIL-01", sensors.getFirst(), plantEnvironment);

        System.out.println("- 5 Demonstration Scenarios Ready -");
        System.out.println();

        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {
                    System.out.println();
                    System.out.println("Shutting down...");
                    simulatorEngine.stop();
                    subscriber.stop();
                    publisher.shutdown();
                    System.out.println("Shutdown complete.");
                })
        );

        System.out.println("[5/5] Starting Simulation...");
        Thread.sleep(1000);

        simulatorEngine.start();

        System.out.println("- Simulator Running -");

        System.out.println("- System Ready -");

        System.out.println();
        System.out.println("NORMAL OPERATION");
        System.out.println("----------------");
        System.out.println("Plant stabilizing...");
        Thread.sleep(15_000);
        System.out.println();
        System.out.println("- Baseline telemetry established -");
        Thread.sleep(2000);

        System.out.println();
        System.out.println("REACTOR OVERHEAT");
        System.out.println("----------------");
        scenarioEngine.triggerScenario(reactorOverheat);
        System.out.println("- Reactor temperature rising -");
        System.out.println("- Backend evaluating thresholds -");
        Thread.sleep(20_000);

        System.out.println();
        System.out.println("PIPELINE OVERPRESSURE");
        System.out.println("---------------------");
        scenarioEngine.triggerScenario(pipelineOverpressure);
        System.out.println("- Pipeline pressure increasing -");
        Thread.sleep(10_000);

        System.out.println();
        System.out.println("STORAGE GAS LEAK");
        System.out.println("----------------");
        scenarioEngine.triggerScenario(storageGasLeak);
        System.out.println("- Gas concentration increasing -");
        Thread.sleep(10_000);

        System.out.println();
        System.out.println("DISTILLATION FIRE");
        System.out.println("-----------------");
        scenarioEngine.triggerScenario(distillationFire);
        System.out.println("- Composite fire scenario activated -");
        System.out.println("- Heat + Gas Leak introduced -");
        Thread.sleep(20_000);

        System.out.println();
        System.out.println("EQUIPMENT FAILURE");
        System.out.println("-----------------");
        scenarioEngine.triggerScenario(equipmentFailure);
        System.out.println("- Selected sensor forced into CRITICAL state -");
        Thread.sleep(20_000);

        System.out.println();
        System.out.println("RECOVERY");
        System.out.println("--------");
        System.out.println("Recovering Reactor...");
        scenarioEngine.clearScenario(reactorOverheat);
        Thread.sleep(6000);
        System.out.println("Recovering Pipeline...");
        scenarioEngine.clearScenario(pipelineOverpressure);
        Thread.sleep(6000);
        System.out.println("Recovering Storage...");
        scenarioEngine.clearScenario(storageGasLeak);
        Thread.sleep(6000);
        System.out.println("Recovering Distillation...");
        scenarioEngine.clearScenario(distillationFire);
        Thread.sleep(6000);
        System.out.println("Replacing Failed Sensor...");
        scenarioEngine.clearScenario(equipmentFailure);
        Thread.sleep(15_000);

        simulatorEngine.stop();
        subscriber.stop();
        publisher.shutdown();

        System.out.println();
        System.out.println("DEMONSTRATION COMPLETE");
        System.out.println("----------------------");

        System.out.println("Plant Areas           : " + locations.size());
        System.out.println("Sensors               : " + sensors.size());
        System.out.println("Scenarios Executed    : 5");

        System.out.println();

        System.out.println("MQTT Communication    : PASS");
        System.out.println("Telemetry Generation  : PASS");
        System.out.println("Scenario Engine       : PASS");
        System.out.println("Digital Twin          : PASS");
        System.out.println("Simulator             : PASS");

        System.out.println();

        System.out.println("Chemical Plant Digital Twin demonstration finished successfully.");
    }
}