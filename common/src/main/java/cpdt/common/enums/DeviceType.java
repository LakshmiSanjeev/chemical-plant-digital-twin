package cpdt.common.enums;

public enum DeviceType {
    // Reactor unit
    // could be deployed anywhere with separate location enum
    // else there could separate devices later
    TEMPERATURE_SENSOR,
    PRESSURE_SENSOR,
    FLOW_SENSOR,
    CONTROL_VALVE,

    // Storage tank area
    LEVEL_SENSOR,
    GAS_SENSOR,
    PH_SENSOR,

    // Cooling system
    VIBRATION_SENSOR,
    PUMP,
    TANK,
    HEAT_EXCHANGER,

    // Electrical units
    CURRENT_SENSOR,
    VOLTAGE_MONITOR
}