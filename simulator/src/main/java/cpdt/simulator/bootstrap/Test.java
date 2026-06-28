package cpdt.simulator.bootstrap;

import cpdt.common.enums.DeviceType;
import cpdt.common.enums.ProcessArea;
import cpdt.common.models.Location;
import cpdt.common.utils.DeviceIdGenerator;
import cpdt.simulator.SensorDevice;
import cpdt.simulator.devices.*;
import cpdt.simulator.engine.*;
import cpdt.simulator.environment.PlantEnvironment;
import cpdt.simulator.mqtt.MqttTelemetryPublisher;
import cpdt.simulator.scenarios.*;

import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args) throws Exception {

        PlantEnvironment plantEnvironment = new PlantEnvironment();
        ScenarioEngine scenarioEngine = new ScenarioEngine(plantEnvironment);

        List<Location> locations = List.of(
                new Location("LOC-001","Reactor Section",ProcessArea.REACTOR_SECTION),
                new Location("LOC-002","Storage Section",ProcessArea.STORAGE_SECTION),
                new Location("LOC-003","Feed Section",ProcessArea.FEED_SECTION),
                new Location("LOC-004","Distillation Section",ProcessArea.DISTILLATION_SECTION),
                new Location("LOC-005","Cooling Section",ProcessArea.COOLING_SECTION),
                new Location("LOC-006","Utilities Section",ProcessArea.UTILITIES_SECTION),
                new Location("LOC-007","Pipeline Section",ProcessArea.PIPELINE_SECTION)
        );

        List<SensorDevice> sensors = new ArrayList<>();

        for(Location location : locations){

            for(int i=1;i<=3;i++){
                sensors.add(new TemperatureSensor(
                        DeviceIdGenerator.generateDeviceId(DeviceType.TEMPERATURE_SENSOR),
                        location.area().name() + "-RTD-" + i,
                        location,
                        plantEnvironment));
            }

            for(int i=1;i<=3;i++){
                sensors.add(new PressureSensor(
                        DeviceIdGenerator.generateDeviceId(DeviceType.PRESSURE_SENSOR),
                        location.area().name() + "-PT-" + i,
                        location,
                        plantEnvironment));
            }

            for(int i=1;i<=2;i++){
                sensors.add(new FlowSensor(
                        DeviceIdGenerator.generateDeviceId(DeviceType.FLOW_SENSOR),
                        location.area().name() + "-FT-" + i,
                        location,
                        plantEnvironment));
            }

            for(int i=1;i<=2;i++){
                sensors.add(new LevelSensor(
                        DeviceIdGenerator.generateDeviceId(DeviceType.LEVEL_SENSOR),
                        location.area().name() + "-LT-" + i,
                        location,
                        plantEnvironment));
            }

            for(int i=1;i<=2;i++){
                sensors.add(new GasSensor(
                        DeviceIdGenerator.generateDeviceId(DeviceType.GAS_SENSOR),
                        location.area().name() + "-GT-" + i,
                        location,
                        plantEnvironment));
            }

            for(int i=1;i<=2;i++){
                sensors.add(new PhSensor(
                        DeviceIdGenerator.generateDeviceId(DeviceType.PH_SENSOR),
                        location.area().name() + "-PHT-" + i,
                        location,
                        plantEnvironment));
            }
        }

        System.out.println("=======================================");
        System.out.println("CPDT MQTT STRESS TEST");
        System.out.println("Sensors Created : " + sensors.size());
        System.out.println("Locations        : " + locations.size());
        System.out.println("=======================================");

        MqttTelemetryPublisher publisher =
                new MqttTelemetryPublisher(
                        "tcp://localhost:1883",
                        "cpdt-simulator-stress-test"
                );

        SimulatorEngine simulatorEngine =
                new SimulatorEngine(
                        sensors,
                        plantEnvironment,
                        publisher,
                        scenarioEngine
                );

        OverheatScenario reactorOverheat =
                new OverheatScenario(
                        "SCN-HEAT-01",
                        ProcessArea.REACTOR_SECTION,
                        plantEnvironment,
                        80.0
                );

        OverpressureScenario pipelineOverpressure =
                new OverpressureScenario(
                        "SCN-PRESS-01",
                        ProcessArea.PIPELINE_SECTION,
                        plantEnvironment,
                        8.0
                );

        FireScenario distillationFire =
                new FireScenario(
                        "SCN-FIRE-01",
                        ProcessArea.DISTILLATION_SECTION,
                        plantEnvironment
                );

        GasLeakScenario storageGasLeak =
                new GasLeakScenario(
                        "SCN-GAS-01",
                        ProcessArea.STORAGE_SECTION,
                        plantEnvironment,
                        450.0
                );

        EquipmentFailureScenario equipmentFailure =
                new EquipmentFailureScenario(
                        "SCN-FAIL-01",
                        sensors.getFirst(),
                        plantEnvironment
                );

        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {
                    simulatorEngine.stop();
                    publisher.shutdown();
                })
        );

        simulatorEngine.start();

        System.out.println("Baseline Phase...");
        Thread.sleep(10_000);

        System.out.println("Trigger Reactor Overheat");
        scenarioEngine.triggerScenario(reactorOverheat);

        Thread.sleep(10_000);

        System.out.println("Trigger Pipeline Overpressure");
        scenarioEngine.triggerScenario(pipelineOverpressure);

        Thread.sleep(10_000);

        System.out.println("Trigger Distillation Fire");
        scenarioEngine.triggerScenario(distillationFire);

        Thread.sleep(10_000);

        System.out.println("Trigger Storage Gas Leak");
        scenarioEngine.triggerScenario(storageGasLeak);

        Thread.sleep(10_000);

        System.out.println("Trigger Equipment Failure");
        scenarioEngine.triggerScenario(equipmentFailure);

        Thread.sleep(20_000);

        System.out.println("Clear Reactor Overheat");
        scenarioEngine.clearScenario(reactorOverheat);

        Thread.sleep(10_000);

        System.out.println("Clear Pipeline Overpressure");
        scenarioEngine.clearScenario(pipelineOverpressure);

        Thread.sleep(10_000);

        System.out.println("Clear Distillation Fire");
        scenarioEngine.clearScenario(distillationFire);

        Thread.sleep(10_000);

        System.out.println("Clear Storage Gas Leak");
        scenarioEngine.clearScenario(storageGasLeak);

        Thread.sleep(10_000);

        System.out.println("Clear Equipment Failure");
        scenarioEngine.clearScenario(equipmentFailure);

        Thread.sleep(30_000);

        simulatorEngine.stop();
        publisher.shutdown();

        System.out.println("MQTT Stress Test Complete");
    }
}