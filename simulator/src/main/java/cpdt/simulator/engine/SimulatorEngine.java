package cpdt.simulator.engine;

import cpdt.common.dto.TelemetryPacket;
import cpdt.common.enums.DeviceStatus;
import cpdt.simulator.SensorDevice;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Coordinates the execution of the chemical plant simulation.
 * <p>
 * The SimulatorEngine advances simulation time, updates active scenarios,
 * schedules sensor sampling, constructs telemetry packets, and publishes
 * measurements through the configured telemetry publisher. It serves as the
 * central orchestrator of the simulator.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
public class SimulatorEngine {

    private static final long ENGINE_TICK_MS = 100;

    private final List<SensorDevice> sensors;
    private final TelemetryPublisher telemetryPublisher;
    private final ScenarioEngine scenarioEngine;

    private ScheduledExecutorService scheduler;

    /**
     * Creates a new simulator engine with the required simulation components.
     *
     * @param sensors the sensors participating in the simulation
     * @param telemetryPublisher the publisher used to transmit telemetry
     * @param scenarioEngine the engine responsible for managing scenarios
     * @throws NullPointerException if any argument is null
     */
    public SimulatorEngine(List<SensorDevice> sensors, TelemetryPublisher telemetryPublisher, ScenarioEngine scenarioEngine) {
        this.sensors = Objects.requireNonNull(sensors, "Sensors cannot be null");
        this.telemetryPublisher = Objects.requireNonNull(telemetryPublisher, "TelemetryPublisher cannot be null");
        this.scenarioEngine = Objects.requireNonNull(scenarioEngine, "ScenarioEngine cannot be null");
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
    /**
     * Executes a single simulation cycle by updating scenarios, sampling
     * eligible sensors, and publishing the resulting telemetry.
     */
    private void simulationTick() {
        try {
            scenarioEngine.update(ENGINE_TICK_MS);
        }
        catch (Exception e) {
            System.err.println("Error updating scenario engine during tick processing");
        }

        long tickTimestamp = System.currentTimeMillis();
        for (SensorDevice sensor : sensors) {
            try {
                long elapsed = tickTimestamp - sensor.getLastSampleTimestamp();
                if (elapsed < sensor.getSamplingIntervalMs()) {
                    continue;
                }
                double reading = sensor.getReading();
                if (sensor.getStatus() == DeviceStatus.CRITICAL) {
                    reading = 0.0;
                }
                sensor.setCurrentValue(reading);
                sensor.setLastUpdated(tickTimestamp);
                sensor.setLastSampleTimestamp(tickTimestamp);
                publishTelemetry(sensor, reading, tickTimestamp);

            }
            catch (Exception e) {
                System.err.println("Failed to sample sensor: " + sensor.getDeviceId());
            }
        }
    }
    /**
     * Creates and publishes a telemetry packet for the specified sensor reading.
     *
     * @param sensor the sensor that produced the measurement
     * @param reading the measured sensor value
     * @param timestamp the time at which the reading was obtained
     */
    private void publishTelemetry(SensorDevice sensor, double reading, long timestamp) {
        try {
            var location = sensor.getLocation();
            TelemetryPacket packet = new TelemetryPacket(
                                sensor.getDeviceId(),
                                sensor.getName(),
                                sensor.getType(),
                                sensor.getStatus(),
                                location.locationId(),
                                location.name(),
                                location.area(),
                                timestamp,
                                sensor.getMeasurementType(),
                                reading);
            telemetryPublisher.publish(packet);
        }
        catch (Exception e) {
            System.err.println("Failed to publish telemetry for sensor: " + sensor.getDeviceId());
        }
    }
    /**
     * Updates the operational status of the sensor with the specified device ID.
     *
     * @param deviceId the unique identifier of the device
     * @param status the new device status to apply
     */
    public void updateDeviceStatus(String deviceId, DeviceStatus status) {
        for (SensorDevice sensor : sensors) {
            if (sensor.getDeviceId().equals(deviceId)) {
                sensor.setStatus(status);
                System.out.println("Updated " + deviceId + " to " + status);
                return;
            }
        }
    }
}