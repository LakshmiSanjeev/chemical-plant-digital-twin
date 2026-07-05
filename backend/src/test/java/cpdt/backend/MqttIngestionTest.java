package cpdt.backend;

import cpdt.backend.config.MqttIngestionConfig;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class MqttIngestionTest {

    private MqttIngestionConfig config;

    @BeforeEach
    void setUp() {
        config = new MqttIngestionConfig();
        ReflectionTestUtils.setField(config, "brokerUrl", "tcp://localhost:1883");
        ReflectionTestUtils.setField(config, "clientId", "test-client");
        ReflectionTestUtils.setField(config, "topic", "cpdt/telemetry");
        ReflectionTestUtils.setField(config, "qos", 1);
    }

    @Test
    void shouldCreateMqttClientFactory() {
        MqttPahoClientFactory factory = config.mqttClientFactory();
        assertNotNull(factory);
        assertInstanceOf(DefaultMqttPahoClientFactory.class, factory);
        
        DefaultMqttPahoClientFactory defaultFactory = (DefaultMqttPahoClientFactory) factory;
        MqttConnectOptions options = defaultFactory.getConnectionOptions();
        assertNotNull(options);
        assertArrayEquals(new String[]{"tcp://localhost:1883"}, options.getServerURIs());
        assertTrue(options.isAutomaticReconnect());
        assertFalse(options.isCleanSession());
        assertEquals(10, options.getConnectionTimeout());
        assertEquals(30, options.getKeepAliveInterval());
    }

    @Test
    void shouldCreateInputChannel() {
        MessageChannel channel = config.mqttInputChannel();
        assertNotNull(channel);
        assertInstanceOf(DirectChannel.class, channel);
    }

    @Test
    void shouldCreateOutputChannel() {
        MessageChannel channel = config.mqttOutputChannel();
        assertNotNull(channel);
        assertInstanceOf(DirectChannel.class, channel);
    }

    @Test
    void shouldCreateInboundAdapter() {
        MessageProducer producer = config.mqttInboundAdapter();
        assertNotNull(producer);
        assertInstanceOf(MqttPahoMessageDrivenChannelAdapter.class, producer);
    }

    @Test
    void shouldCreateOutboundHandler() {
        MqttPahoMessageHandler handler = config.mqttOutboundHandler();
        assertNotNull(handler);
        assertInstanceOf(MqttPahoMessageHandler.class, handler);
    }
}