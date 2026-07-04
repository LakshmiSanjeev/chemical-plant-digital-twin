/**
 * Provides MQTT communication components for publishing simulated
 * telemetry to external systems and vice versa.
 *
 * <p>This package contains the MQTT client implementation responsible
 * for establishing broker connections, serializing telemetry data, and
 * publishing messages to the appropriate MQTT topics. It enables the
 * simulator to communicate with downstream consumers, such as the
 * backend digital twin as well as receive data from the backend
 * for updates of device status.</p>
 */

package cpdt.simulator.mqtt;