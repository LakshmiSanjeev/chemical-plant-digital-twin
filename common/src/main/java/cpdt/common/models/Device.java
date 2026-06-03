package cpdt.common.models;

import cpdt.common.enums.DeviceStatus;
import cpdt.common.enums.DeviceType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public abstract class Device {

    @EqualsAndHashCode.Include
    private final String deviceId;
    private final String name;
    private final DeviceType type;
    private final Location location;

    @Setter
    private DeviceStatus status;
    @Setter
    private long lastUpdated;

    protected Device(String deviceId, String name, DeviceType type, Location location, DeviceStatus status){
        this.deviceId = deviceId;
        this.name = name;
        this.type = type;
        this.location = location;
        this.status = status;
        this.lastUpdated = System.currentTimeMillis();
    }
}
