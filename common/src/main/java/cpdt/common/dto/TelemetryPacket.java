package cpdt.common.dto;

import cpdt.common.enums.DeviceStatus;
import cpdt.common.enums.DeviceType;
import cpdt.common.enums.MeasurementType;

public record TelemetryPacket(
        String deviceId,
        DeviceType deviceType,
        DeviceStatus status,
        long timestamp,
        MeasurementType measurementType,
        double value
) {}
