package cpdt.common.models;

import cpdt.common.enums.DeviceStatus;
import cpdt.common.enums.DeviceType;

public abstract class Device {

    private String deviceId;
    private String name;
    private DeviceType type;
    private Location location;
    private DeviceStatus status;

    protected Device(){}

    protected Device(String deviceId, String name, DeviceType type, Location location, DeviceStatus status){
        this.deviceId = deviceId;
        this.name = name;
        this.type = type;
        this.location = location;
        this.status = status;
    }
}
