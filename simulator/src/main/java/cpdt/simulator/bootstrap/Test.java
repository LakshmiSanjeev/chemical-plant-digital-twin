package cpdt.simulator.bootstrap;

import cpdt.common.enums.DeviceType;
import cpdt.common.enums.ProcessArea;
import cpdt.common.models.Location;
import cpdt.common.utils.DeviceIdGenerator;
import cpdt.simulator.SensorDevice;
import cpdt.simulator.devices.*;
import cpdt.simulator.engine.*;
import cpdt.simulator.environment.PlantEnvironment;

import java.util.List;

public class Test {

    public static void main(String[] args)
            throws InterruptedException {

        PlantEnvironment plantEnvironment = new PlantEnvironment();

        Location reactor =
                new Location(
                        "LOC-001",
                        "Reactor Section",
                        ProcessArea.REACTOR_SECTION
                );

        Location storage =
                new Location(
                        "LOC-002",
                        "Storage Section",
                        ProcessArea.STORAGE_SECTION
                );

        Location feed =
                new Location(
                        "LOC-003",
                        "Feed Section",
                        ProcessArea.FEED_SECTION
                );

        Location distillation =
                new Location(
                        "LOC-004",
                        "Distillation Section",
                        ProcessArea.DISTILLATION_SECTION
                );

        Location cooling =
                new Location(
                        "LOC-005",
                        "Cooling Section",
                        ProcessArea.COOLING_SECTION
                );

        Location utilities =
                new Location(
                        "LOC-006",
                        "Utilities Section",
                        ProcessArea.UTILITIES_SECTION
                );

        Location pipeline =
                new Location(
                        "LOC-007",
                        "Pipeline Section",
                        ProcessArea.PIPELINE_SECTION
                );

        SensorDevice reactorTemperature =
                new TemperatureSensor(
                        DeviceIdGenerator.generateDeviceId(
                                DeviceType.TEMPERATURE_SENSOR),
                        "RTD-101",
                        reactor,
                        plantEnvironment
                );

        SensorDevice distillationTemperature =
                new TemperatureSensor(
                        DeviceIdGenerator.generateDeviceId(
                                DeviceType.TEMPERATURE_SENSOR),
                        "RTD-201",
                        distillation,
                        plantEnvironment
                );

        SensorDevice reactorPressure =
                new PressureSensor(
                        DeviceIdGenerator.generateDeviceId(
                                DeviceType.PRESSURE_SENSOR),
                        "PT-101",
                        reactor,
                        plantEnvironment
                );

        SensorDevice pipelinePressure =
                new PressureSensor(
                        DeviceIdGenerator.generateDeviceId(
                                DeviceType.PRESSURE_SENSOR),
                        "PT-201",
                        pipeline,
                        plantEnvironment
                );

        SensorDevice feedFlow =
                new FlowSensor(
                        DeviceIdGenerator.generateDeviceId(
                                DeviceType.FLOW_SENSOR),
                        "FT-101",
                        feed,
                        plantEnvironment
                );

        SensorDevice pipelineFlow =
                new FlowSensor(
                        DeviceIdGenerator.generateDeviceId(
                                DeviceType.FLOW_SENSOR),
                        "FT-201",
                        pipeline,
                        plantEnvironment
                );

        SensorDevice feedGas =
                new GasSensor(
                        DeviceIdGenerator.generateDeviceId(
                                DeviceType.GAS_SENSOR),
                        "GT-101",
                        feed,
                        plantEnvironment
                );

        SensorDevice reactorGas =
                new GasSensor(
                        DeviceIdGenerator.generateDeviceId(
                                DeviceType.GAS_SENSOR),
                        "GT-201",
                        reactor,
                        plantEnvironment
                );

        SensorDevice storageLevel =
                new LevelSensor(
                        DeviceIdGenerator.generateDeviceId(
                                DeviceType.LEVEL_SENSOR),
                        "LT-101",
                        storage,
                        plantEnvironment
                );

        SensorDevice coolingLevel =
                new LevelSensor(
                        DeviceIdGenerator.generateDeviceId(
                                DeviceType.LEVEL_SENSOR),
                        "LT-201",
                        cooling,
                        plantEnvironment
                );

        SensorDevice coolingPh =
                new PhSensor(
                        DeviceIdGenerator.generateDeviceId(
                                DeviceType.PH_SENSOR),
                        "PHT-101",
                        cooling,
                        plantEnvironment
                );

        SensorDevice utilitiesPh =
                new PhSensor(
                        DeviceIdGenerator.generateDeviceId(
                                DeviceType.PH_SENSOR),
                        "PHT-201",
                        utilities,
                        plantEnvironment
                );

        List<SensorDevice> sensors = List.of(
                reactorTemperature,
                distillationTemperature,
                reactorPressure,
                pipelinePressure,
                feedFlow,
                pipelineFlow,
                feedGas,
                reactorGas,
                storageLevel,
                coolingLevel,
                coolingPh,
                utilitiesPh
        );

        SimulatorEngine simulatorEngine =
                new SimulatorEngine(
                        sensors,
                        plantEnvironment,
                        new ConsoleTelemetryPublisher()
                );

        Runtime.getRuntime().addShutdownHook(
                new Thread(simulatorEngine::stop)
        );

        System.out.println();
        System.out.println("========================================");
        System.out.println("CPDT SIMULATOR INTEGRATION SMOKE TEST");
        System.out.println("Sensors Registered : " + sensors.size());
        System.out.println("Process Areas      : " + ProcessArea.values().length);
        System.out.println("Duration           : 30 seconds");
        System.out.println("========================================");
        System.out.println();

        simulatorEngine.start();

        Thread.sleep(30_000);

        simulatorEngine.stop();

        System.out.println();
        System.out.println("Smoke test completed successfully.");
    }
}