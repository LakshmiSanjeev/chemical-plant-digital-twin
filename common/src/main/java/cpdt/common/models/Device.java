package cpdt.common.models;

import cpdt.common.enums.DeviceStatus;
import cpdt.common.enums.DeviceType;

public class Device {
    private String deviceId;
    private String name;

    private DeviceType type;

    private Location location;

    private DeviceStatus status;
}
