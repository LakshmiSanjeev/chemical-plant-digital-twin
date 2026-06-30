package cpdt.common.utils;

import cpdt.common.dto.TelemetryPacket;
import cpdt.common.enums.DeviceStatus;
import cpdt.common.enums.DeviceType;
import cpdt.common.enums.MeasurementType;
import cpdt.common.enums.ProcessArea;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TopicBuilderTest {

    @Test
    void shouldBuildCorrectTopic() {
        TelemetryPacket packet = new TelemetryPacket(
                "TEMP-001",
                "Reactor Temperature Sensor",
                DeviceType.TEMPERATURE_SENSOR,
                DeviceStatus.ONLINE,
                "LOC-001",
                "Reactor",
                ProcessArea.REACTOR_SECTION,
                System.currentTimeMillis(),
                MeasurementType.TEMPERATURE,
                45.7
        );
        String topic = TopicBuilder.build(packet);
        assertEquals("cpdt/telemetry/REACTOR_SECTION/TEMPERATURE_SENSOR/TEMP-001", topic);
    }

    @Test
    void shouldBuildDifferentTopicsForDifferentPackets() {
        TelemetryPacket firstPacket = new TelemetryPacket(
                "TEMP-001",
                "Temperature Sensor",
                DeviceType.TEMPERATURE_SENSOR,
                DeviceStatus.ONLINE,
                "LOC-001",
                "Reactor",
                ProcessArea.REACTOR_SECTION,
                System.currentTimeMillis(),
                MeasurementType.TEMPERATURE,
                50.0
        );
        TelemetryPacket secondPacket = new TelemetryPacket(
                "PRESS-001",
                "Pressure Sensor",
                DeviceType.PRESSURE_SENSOR,
                DeviceStatus.ONLINE,
                "LOC-002",
                "Pipeline",
                ProcessArea.PIPELINE_SECTION,
                System.currentTimeMillis(),
                MeasurementType.PRESSURE,
                12.3
        );
        String firstTopic = TopicBuilder.build(firstPacket);
        String secondTopic = TopicBuilder.build(secondPacket);
        assertNotEquals(firstTopic, secondTopic);
    }

    @Test
    void shouldContainExpectedTopicSegments() {
        TelemetryPacket packet = new TelemetryPacket(
                "FLOW-005",
                "Flow Sensor",
                DeviceType.FLOW_SENSOR,
                DeviceStatus.WARNING,
                "LOC-010",
                "Feed",
                ProcessArea.FEED_SECTION,
                System.currentTimeMillis(),
                MeasurementType.FLOW_RATE,
                95.2
        );
        String topic = TopicBuilder.build(packet);
        assertTrue(topic.startsWith("cpdt/telemetry/"));
        assertTrue(topic.contains("FEED_SECTION"));
        assertTrue(topic.contains("FLOW_SENSOR"));
        assertTrue(topic.endsWith("FLOW-005"));
    }

    @Test
    void shouldThrowExceptionWhenPacketIsNull() {
        assertThrows(NullPointerException.class, () -> TopicBuilder.build(null));
    }
}