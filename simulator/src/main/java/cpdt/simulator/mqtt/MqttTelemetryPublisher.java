package cpdt.simulator.mqtt;

import cpdt.common.dto.TelemetryPacket;
import cpdt.common.utils.TelemetrySerializer;
import cpdt.common.utils.TopicBuilder;
import cpdt.simulator.engine.TelemetryPublisher;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayDeque;
import java.util.Queue;

public class MqttTelemetryPublisher implements TelemetryPublisher {

    private static final int QOS = 1;
    private static final int MAX_BUFFER_SIZE = 10_000;

    private final MqttClient mqttClient;
    private final MqttConnectOptions connectOptions;

    private final Queue<TelemetryPacket> retryBuffer;

    public MqttTelemetryPublisher(String brokerUrl,
                                  String clientId) throws MqttException {

        this.retryBuffer = new ArrayDeque<>();

        this.mqttClient = new MqttClient(
                brokerUrl,
                clientId,
                new MemoryPersistence()
        );

        this.connectOptions = new MqttConnectOptions();
        this.connectOptions.setAutomaticReconnect(true);
        this.connectOptions.setCleanSession(true);
        this.connectOptions.setConnectionTimeout(10);
        this.connectOptions.setKeepAliveInterval(30);

        mqttClient.setCallback(new MqttCallback() {

            @Override
            public void connectionLost(Throwable cause) {
                System.err.println(
                        "MQTT connection lost: "
                                + (cause != null ? cause.getMessage() : "unknown")
                );
            }

            @Override
            public void messageArrived(String topic,
                                       MqttMessage message) {
                // publisher only
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // optional logging
            }
        });

        connect();
    }

    private synchronized void connect() throws MqttException {

        if (mqttClient.isConnected()) {
            return;
        }

        mqttClient.connect(connectOptions);

        System.out.println(
                "Connected to MQTT broker: "
                        + mqttClient.getServerURI()
        );
    }

    @Override
    public synchronized void publish(TelemetryPacket packet) {

        if (packet == null) {
            return;
        }

        try {

            ensureConnected();

            flushRetryBuffer();

            publishInternal(packet);
        }
        catch (Exception e) {

            queueForRetry(packet);

            System.err.println(
                    "Failed to publish telemetry. Added to retry queue."
            );
        }
    }

    private void publishInternal(TelemetryPacket packet)
            throws MqttException {

        String topic = TopicBuilder.build(packet);

        byte[] payload =
                TelemetrySerializer.toJson(packet);

        MqttMessage mqttMessage =
                new MqttMessage(payload);

        mqttMessage.setQos(QOS);

        mqttClient.publish(topic, mqttMessage);
    }

    private void flushRetryBuffer() {

        if (!mqttClient.isConnected()) {
            return;
        }

        while (!retryBuffer.isEmpty()) {

            TelemetryPacket packet = retryBuffer.peek();

            try {

                publishInternal(packet);

                retryBuffer.poll();
            }
            catch (Exception e) {
                break;
            }
        }
    }

    private void queueForRetry(TelemetryPacket packet) {

        if (retryBuffer.size() >= MAX_BUFFER_SIZE) {
            retryBuffer.poll();
        }

        retryBuffer.offer(packet);
    }

    private void ensureConnected() throws MqttException {

        if (!mqttClient.isConnected()) {
            connect();
        }
    }

    public synchronized void shutdown() {

        try {

            if (mqttClient.isConnected()) {
                mqttClient.disconnect();
            }

            mqttClient.close();

            System.out.println(
                    "MQTT publisher shutdown completed."
            );
        }
        catch (Exception e) {

            System.err.println(
                    "Error during MQTT shutdown: "
                            + e.getMessage()
            );
        }
    }
}