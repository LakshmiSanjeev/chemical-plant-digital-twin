package cpdt.backend.mqtt;

import cpdt.common.dto.DeviceStatusMessage;
import cpdt.common.utils.TelemetrySerializer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DeviceStatusPublisher {

    private final MqttClient mqttClient;
    private final MqttConnectOptions connectOptions;

    private final int qos;

    public DeviceStatusPublisher(
            @Value("${mqtt.broker}") String brokerUrl,
            @Value("${mqtt.client-id}") String clientId,
            @Value("${mqtt.qos}") int qos
    ) throws MqttException {

        this.qos = qos;

        this.mqttClient = new MqttClient(
                brokerUrl,
                clientId + "-status-publisher",
                new MemoryPersistence()
        );

        this.connectOptions = new MqttConnectOptions();
        this.connectOptions.setAutomaticReconnect(true);
        this.connectOptions.setCleanSession(true);
        this.connectOptions.setConnectionTimeout(10);
        this.connectOptions.setKeepAliveInterval(30);
    }

    @PostConstruct
    public void connect() throws MqttException {

        if (!mqttClient.isConnected()) {
            mqttClient.connect(connectOptions);
        }
    }

    public synchronized void publish(DeviceStatusMessage message) {

        try {

            String topic =
                    "cpdt/device-status/" + message.deviceId();

            byte[] payload =
                    TelemetrySerializer.toJson(message);

            MqttMessage mqttMessage =
                    new MqttMessage(payload);

            mqttMessage.setQos(qos);

            mqttClient.publish(topic, mqttMessage);

        } catch (Exception e) {

            System.err.println(
                    "Failed to publish device status: "
                            + e.getMessage()
            );
        }
    }

    @PreDestroy
    public void shutdown() {

        try {

            if (mqttClient.isConnected()) {
                mqttClient.disconnect();
            }

            mqttClient.close();

        } catch (Exception ignored) {
        }
    }
}