package cpdt.backend.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;

/**
 * Configures MQTT messaging infrastructure for the backend application.
 *
 * <p>This configuration class creates and initializes the MQTT client,
 * inbound and outbound messaging channels, and the corresponding message
 * adapters required for communication with the simulator. It enables the
 * backend to receive telemetry data and publish device status updates
 * through the MQTT broker.
 *
 * <p>The configuration relies on application properties for broker
 * connection details and Quality of Service (QoS) settings while using
 * Spring Integration to manage MQTT message flow.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
@Configuration
@IntegrationComponentScan
public class MqttIngestionConfig {

    @Value("${mqtt.broker}")
    private String brokerUrl;

    @Value("${mqtt.client-id}")
    private String clientId;

    @Value("${mqtt.topic}")
    private String topic;

    @Value("${mqtt.qos}")
    private int qos;

    /**
     * Creates the MQTT client factory used for establishing broker connections.
     *
     * <p>The factory configures connection parameters including automatic
     * reconnection, persistent sessions, connection timeout, and keep-alive
     * interval for all MQTT clients used by the backend.
     *
     * @return the configured MQTT client factory
     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{brokerUrl});
        options.setAutomaticReconnect(true);
        options.setCleanSession(false);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(30);
        factory.setConnectionOptions(options);
        return factory;
    }
    /**
     * Creates the message channel for incoming MQTT telemetry messages.
     *
     * <p>This channel receives messages published by the MQTT inbound adapter
     * before they are processed by the telemetry ingestion pipeline.
     *
     * @return the MQTT input message channel
     */
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }
    /**
     * Creates the message channel for outgoing MQTT messages.
     *
     * <p>This channel is used to route backend-generated messages, such as
     * device status updates, to the MQTT outbound handler for publication.
     *
     * @return the MQTT output message channel
     */
    @Bean
    public MessageChannel mqttOutputChannel() {
        return new DirectChannel();
    }
    /**
     * Creates the MQTT inbound message adapter for telemetry ingestion.
     *
     * <p>The adapter subscribes to the configured MQTT topic, converts
     * received payloads into byte arrays, and forwards them to the MQTT
     * input channel for further processing by the backend.
     *
     * @return the configured MQTT inbound message producer
     */
    @Bean
    public MessageProducer mqttInboundAdapter() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(clientId, mqttClientFactory(), topic);
        adapter.setCompletionTimeout(5000);
        DefaultPahoMessageConverter converter = new DefaultPahoMessageConverter();
        converter.setPayloadAsBytes(true);
        adapter.setConverter(converter);
        adapter.setQos(qos);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }
    /**
     * Creates the MQTT outbound message handler for publishing messages.
     *
     * <p>The handler publishes backend-generated messages to the MQTT broker
     * using the configured Quality of Service (QoS) level and asynchronous
     * message delivery.
     *
     * @return the configured MQTT outbound message handler
     */
    @Bean
    public MqttPahoMessageHandler mqttOutboundHandler() {
        MqttPahoMessageHandler handler = new MqttPahoMessageHandler(clientId + "-publisher", mqttClientFactory());
        handler.setAsync(true);
        handler.setDefaultQos(qos);
        handler.setDefaultRetained(false);
        handler.setConverter(new DefaultPahoMessageConverter());
        return handler;
    }
}