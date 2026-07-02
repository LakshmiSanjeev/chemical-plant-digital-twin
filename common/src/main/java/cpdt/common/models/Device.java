package cpdt.common.models;

import cpdt.common.enums.DeviceStatus;
import cpdt.common.enums.DeviceType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Abstract base class representing a physical device within the Chemical
 * Plant Digital Twin Platform.
 *
 * <p>This class encapsulates the common identity, location, and operational
 * state shared by all devices in the system. It serves as the foundation of
 * the device hierarchy, allowing specialized device types such as sensors
 * and actuators to inherit common functionality while providing their own
 * domain-specific behavior.
 *
 * <p>Each device is uniquely identified by its device identifier, while its
 * operational state and last update timestamp may change throughout its
 * lifecycle.
 *
 * <p>Equality is based solely on the device identifier, ensuring that two
 * objects representing the same physical device are considered equal
 * regardless of their current state.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public abstract class Device {

    /**
     * Unique identifier assigned to the device. This identifier remains constant throughout the device's lifetime
     * and is used to uniquely distinguish it from all other devices.
     */
    @EqualsAndHashCode.Include
    private final String deviceId;

    /**
     * Human-readable name of the device.
     */
    private final String name;

    /**
     * Type of the device. Determines the category of hardware represented by this instance,
     * such as a temperature sensor or pressure sensor.
     */
    private final DeviceType type;

    /**
     * Physical location of the device within the simulated plant.
     */
    private final Location location;

    /**
     * Current operational status of the device. The status may change during simulation to reflect normal
     * operation, maintenance, warnings, critical failures, or offline
     * conditions.
     */
    @Setter
    private DeviceStatus status;

    /**
     * Timestamp indicating when the device state was last updated. The value is stored as milliseconds since
     * the Unix epoch.
     */
    @Setter
    private long lastUpdated;

    /**
     * Constructs a new device with the specified identity and location.
     *
     * <p>Newly created devices are initialized with an
     * {@link DeviceStatus#ONLINE} status and the current system time as
     * their initial update timestamp.
     *
     * @param deviceId unique identifier of the device
     * @param name human-readable device name
     * @param type category of device
     * @param location physical location of the device within the plant
     */
    protected Device(String deviceId, String name, DeviceType type, Location location) {
        this.deviceId = deviceId;
        this.name = name;
        this.type = type;
        this.location = location;
        this.status = DeviceStatus.ONLINE;
        this.lastUpdated = System.currentTimeMillis();
    }
}