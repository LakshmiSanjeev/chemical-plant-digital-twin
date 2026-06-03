package cpdt.common.enums;

public enum DeviceStatus {
    ONLINE,
    OFFLINE,
    // could be lesser than 45 percent power/energy for actuators or bigger machines
    // could be staggered I/O detection or connectivity issues for sensors
    WARNING,
    // lesser than 20 percent power/energy for actuators
    // no connection with sensors
    CRITICAL,
    MAINTENANCE
}