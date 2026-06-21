package cpdt.simulator.engine;

import cpdt.common.dto.TelemetryPacket;
import cpdt.simulator.SensorDevice;
import cpdt.simulator.environment.PlantEnvironment;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimulatorEngine {

    private static final long ENGINE_TICK_MS = 100;

    private final List<SensorDevice> sensors;
    private final PlantEnvironment plantEnvironment;
    private final TelemetryPublisher telemetryPublisher;

    private ScheduledExecutorService scheduler;

    public SimulatorEngine(List<SensorDevice> sensors, PlantEnvironment plantEnvironment, TelemetryPublisher telemetryPublisher) {
        this.sensors = Objects.requireNonNull(sensors, "Sensors cannot be null");
        this.plantEnvironment = Objects.requireNonNull(plantEnvironment, "PlantEnvironment cannot be null");
        this.telemetryPublisher = Objects.requireNonNull(telemetryPublisher, "TelemetryPublisher cannot be null");
    }

    public void start() {
        if (scheduler != null && !scheduler.isShutdown()) {
            return;
        }
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::simulationTick, 0, ENGINE_TICK_MS, TimeUnit.MILLISECONDS);
        System.out.println("Simulator Engine started.");
    }

    public void stop() {
        if (scheduler == null) {
            return;
        }
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        }
        catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.out.println("Simulator Engine stopped.");
    }

    private void simulationTick() {
        long tickTimestamp = System.currentTimeMillis();
        for (SensorDevice sensor : sensors) {
            try {
                long elapsed = tickTimestamp - sensor.getLastSampleTimestamp();
                if (elapsed < sensor.getSamplingIntervalMs()) {
                    continue;
                }
                double reading = sensor.getReading();
                sensor.setCurrentValue(reading);
                sensor.setLastUpdated(tickTimestamp);
                sensor.setLastSampleTimestamp(tickTimestamp);
                publishTelemetry(sensor, reading, tickTimestamp);

            }
            catch (Exception e) {
                System.err.println("Failed to sample sensor: " + sensor.getDeviceId());
                e.printStackTrace();
            }
        }
    }

    private void publishTelemetry(SensorDevice sensor, double reading, long timestamp) {
        try {
            var location = sensor.getLocation();
            TelemetryPacket packet =
                    new TelemetryPacket(
                            sensor.getDeviceId(),
                            sensor.getName(),
                            sensor.getType(),
                            sensor.getStatus(),
                            location.locationId(),
                            location.name(),
                            location.area(),
                            timestamp,
                            sensor.getMeasurementType(),
                            reading
                    );
            telemetryPublisher.publish(packet);
        }
        catch (Exception e) {
            System.err.println("Failed to publish telemetry for sensor: " + sensor.getDeviceId());
            e.printStackTrace();
        }
    }
}