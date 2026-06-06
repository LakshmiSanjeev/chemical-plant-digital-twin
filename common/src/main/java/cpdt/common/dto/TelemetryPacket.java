package cpdt.common.dto;

import cpdt.common.enums.DeviceStatus;
import cpdt.common.enums.DeviceType;
import cpdt.common.enums.MeasurementType;

import java.util.Map;

public record TelemetryPacket(
        String deviceId,
        DeviceType deviceType,
        long timestamp,
        MeasurementType measurementType,
        double value,
        DeviceStatus status
) {}