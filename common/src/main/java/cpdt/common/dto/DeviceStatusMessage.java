package cpdt.common.dto;

import cpdt.common.enums.DeviceStatus;

/**
 * Represents a device status update exchanged between system components.
 * It communicates the current operational status of a device together with
 * the reason for the change and the time at which it occurred.
 *
 * @param deviceId identifier of the device
 * @param status current operational status of the device
 * @param reason explanation for the reported status
 * @param timestamp time at which the status update was generated, in milliseconds since the Unix epoch
 */
public record DeviceStatusMessage(
        String deviceId,
        DeviceStatus status,
        String reason,
        long timestamp
) {}