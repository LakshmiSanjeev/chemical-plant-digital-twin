package cpdt.simulator.mqtt;

import cpdt.common.dto.DeviceStatusMessage;
import cpdt.common.utils.TelemetrySerializer;
import cpdt.simulator.engine.SimulatorEngine;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * DeviceStatusSubscriber subscribes to backend-published device status updates
 * over MQTT and applies them to the simulator in real time.
 *
 * <p>
 * It listens for messages on the configured device status topic, deserializes
 * each incoming {@code DeviceStatusMessage}, and forwards the updated status
 * to the {@link SimulatorEngine}. This enables the simulator to reflect
 * device state changes initiated by the backend Digital Twin, such as
 * maintenance, offline, or critical conditions.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */

public class DeviceStatusSubscriber {

    private static final String TOPIC = "cpdt/device-status/#";

    private final MqttClient mqttClient;
    private final MqttConnectOptions connectOptions;

    /**
     * Creates a new MQTT subscriber for receiving device status updates.
     *
     * @param brokerUrl the MQTT broker URL to connect to
     * @param clientId the unique MQTT client identifier
     * @param simulatorEngine the simulator engine that applies received status updates
     * @throws MqttException if the MQTT client cannot be created
     */
    public DeviceStatusSubscriber(String brokerUrl, String clientId, SimulatorEngine simulatorEngine) throws MqttException {
        this.mqttClient = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
        this.connectOptions = new MqttConnectOptions();
        this.connectOptions.setAutomaticReconnect(true);
        this.connectOptions.setCleanSession(true);
        this.connectOptions.setConnectionTimeout(10);
        this.connectOptions.setKeepAliveInterval(30);
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.err.println("DeviceStatusSubscriber disconnected.");
            }
            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) {
                try {
                    DeviceStatusMessage message = TelemetrySerializer.fromJson(mqttMessage.getPayload(), DeviceStatusMessage.class);
                    simulatorEngine.updateDeviceStatus(message.deviceId(), message.status());
                }
                catch (Exception e) {
                    System.err.println("Failed to process device status update.");
                }
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) { }
        });
    }

    /**
     * Connects to the MQTT broker and subscribes to the device status topic.
     *
     * @throws MqttException if the broker connection or subscription fails
     */
    public void start() throws MqttException {
        mqttClient.connect(connectOptions);
        mqttClient.subscribe(TOPIC);
        System.out.println("Subscribed to " + TOPIC);
    }
    /**
     * Disconnects from the MQTT broker and releases all associated MQTT resources.
     */
    public void stop() {
        try {
            if (mqttClient.isConnected()) {mqttClient.disconnect();}
            mqttClient.close();
        }
        catch (Exception ignored) { }
    }
}