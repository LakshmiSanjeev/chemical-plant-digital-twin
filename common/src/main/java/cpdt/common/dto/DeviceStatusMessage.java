package cpdt.common.dto;

import cpdt.common.enums.DeviceStatus;

public record DeviceStatusMessage(
        String deviceId,
        DeviceStatus status,
        String reason,
        long timestamp
) {}