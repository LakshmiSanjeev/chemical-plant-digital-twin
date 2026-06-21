package cpdt.common.dto;

import cpdt.common.enums.*;

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
