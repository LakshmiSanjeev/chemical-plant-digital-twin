package cpdt.simulator.bootstrap;

import cpdt.common.enums.ProcessArea;
import cpdt.common.models.Location;
import cpdt.simulator.devices.TemperatureSensor;
import cpdt.simulator.environment.PlantEnvironment;

public class Test {
    public static void main(String[] args) {
        PlantEnvironment plantEnvironment = new PlantEnvironment();
        Location reactorLocation =
                new Location(
                        "R11",
                        "Reactor 11",
                        ProcessArea.REACTOR_SECTION
                );
        TemperatureSensor sensor =
                new TemperatureSensor(
                        "TEMP-001",
                        "Reactor Temperature Sensor",
                        reactorLocation,
                        plantEnvironment
                );
        System.out.println(sensor.getDeviceId());
        System.out.println(sensor.getType());
        System.out.println(sensor.getMeasurementType());
        System.out.println(sensor.getReading());
    }
}