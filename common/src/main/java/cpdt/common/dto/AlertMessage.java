package cpdt.common.dto;

import cpdt.common.enums.AlertSeverity;

public record AlertMessage(
        String alertId,
        String deviceId,
        AlertSeverity severity,
        String message,
        long timestamp
) {}