package cpdt.common.dto;

import cpdt.common.enums.*;

/**
 * Represents a telemetry reading produced by a simulated device and exchanged
 * between the simulator and backend. A telemetry packet includes device
 * metadata, location information, the measured parameter, and its value.
 *
 * @param deviceId unique identifier of the device
 * @param deviceName display name of the device
 * @param deviceType type of the reporting device
 * @param status current operational status of the device
 * @param locationId identifier of the device location
 * @param locationName display name of the device location
 * @param processArea process area in which the device is installed
 * @param timestamp time at which the reading was captured, in milliseconds since the Unix epoch
 * @param measurementType type of measurement represented by the telemetry value
 * @param value measured value reported by the device
 */
public record TelemetryPacket(
        String deviceId,
        String deviceName,
        DeviceType deviceType,
        DeviceStatus status,
        String locationId,
        String locationName,
        ProcessArea processArea,
        long timestamp,
        MeasurementType measurementType,
        double value
) {}
