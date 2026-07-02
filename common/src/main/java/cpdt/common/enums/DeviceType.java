package cpdt.common.enums;

import lombok.Getter;

/**
 * Enumerates every supported device type that can exist within the
 * Chemical Plant Digital Twin Platform.
 *
 * <p>Contains current implementations and future additions. Each device type is
 * associated with a unique identifier prefix used
 * by {@link cpdt.common.utils.DeviceIdGenerator} when generating device
 * identifiers.
 *
 * @since 1.0
 */
@Getter
public enum DeviceType {
    // Process Sensors
    TEMPERATURE_SENSOR("TEMP"),
    PRESSURE_SENSOR("PRESS"),
    FLOW_SENSOR("FLOW"),
    LEVEL_SENSOR("LEVEL"),
    GAS_SENSOR("GAS"),
    PH_SENSOR("PH"),
    HUMIDITY_SENSOR("HUM"),

    // Mechanical Condition Monitoring
    VIBRATION_SENSOR("VIB"),

    // Actuators
    CONTROL_VALVE("VALVE"),
    PUMP("PUMP"),
    MOTOR("MOT"),

    // Process Equipment
    TANK("TANK"),
    HEAT_EXCHANGER("HX"),

    // Electrical Monitoring
    CURRENT_SENSOR("CURR"),
    VOLTAGE_MONITOR("VOLT");

    private final String prefix;

    DeviceType(String prefix) {
        this.prefix = prefix;
    }
}