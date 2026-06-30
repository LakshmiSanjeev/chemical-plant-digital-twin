package cpdt.common.enums;

/**
 * Represents the current operational status of a device within the
 * Chemical Plant Digital Twin Platform.
 *
 * <p>The device status reflects its operating condition and is used by
 * the simulator and backend to determine device availability,
 * maintenance state, and fault conditions.
 *
 * @since 1.0
 */
public enum DeviceStatus {
    ONLINE,
    OFFLINE,
    WARNING,
    CRITICAL,
    MAINTENANCE,
    RETIRED
}