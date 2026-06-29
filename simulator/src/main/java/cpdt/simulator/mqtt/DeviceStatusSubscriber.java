package cpdt.simulator.mqtt;

import cpdt.common.dto.DeviceStatusMessage;
import cpdt.common.utils.TelemetrySerializer;
import cpdt.simulator.engine.SimulatorEngine;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class DeviceStatusSubscriber {

    private static final String TOPIC = "cpdt/device-status/#";

    private final SimulatorEngine simulatorEngine;
    private final MqttClient mqttClient;
    private final MqttConnectOptions connectOptions;

    public DeviceStatusSubscriber(
            String brokerUrl,
            String clientId,
            SimulatorEngine simulatorEngine
    ) throws MqttException {

        this.simulatorEngine = simulatorEngine;

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
                        "DeviceStatusSubscriber disconnected."
                );
            }

            @Override
            public void messageArrived(
                    String topic,
                    MqttMessage mqttMessage
            ) {

                try {

                    DeviceStatusMessage message =
                            TelemetrySerializer.fromJson(
                                    mqttMessage.getPayload(),
                                    DeviceStatusMessage.class
                            );

                    simulatorEngine.updateDeviceStatus(
                            message.deviceId(),
                            message.status()
                    );

                }
                catch (Exception e) {

                    System.err.println(
                            "Failed to process device status update."
                    );

                    e.printStackTrace();
                }
            }

            @Override
            public void deliveryComplete(
                    IMqttDeliveryToken token
            ) {
                // Subscriber only
            }
        });
    }

    public void start() throws MqttException {

        mqttClient.connect(connectOptions);

        mqttClient.subscribe(TOPIC);

        System.out.println(
                "Subscribed to " + TOPIC
        );
    }

    public void stop() {

        try {

            if (mqttClient.isConnected()) {
                mqttClient.disconnect();
            }

            mqttClient.close();

        }
        catch (Exception ignored) {
        }
    }
}