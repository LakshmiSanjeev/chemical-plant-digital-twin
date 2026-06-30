/**
 * Contains implementations of simulated devices, including sensors and future actuators.
 *
 * <p>Each device models the behavior of a specific industrial instrument,
 * including its physical response characteristics, measurement algorithms,
 * noise sources, drift, and hardware limitations. Sensor implementations
 * extend the {@code SensorDevice} abstraction and generate realistic
 * telemetry based on the current plant environment.
 */
package cpdt.simulator.devices;