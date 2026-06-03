package cpdt.common.enums;

import lombok.Getter;

@Getter
public enum DeviceType {

    // Process Sensors
    TEMPERATURE_SENSOR("TEMP"),
    PRESSURE_SENSOR("PRESS"),
    FLOW_SENSOR("FLOW"),
    LEVEL_SENSOR("LEVEL"),
    GAS_SENSOR("GAS"),
    PH_SENSOR("PH"),

    // Mechanical Condition Monitoring
    VIBRATION_SENSOR("VIB"),

    // Actuators
    CONTROL_VALVE("VALVE"),
    PUMP("PUMP"),

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