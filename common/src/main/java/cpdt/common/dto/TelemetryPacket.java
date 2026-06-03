package cpdt.common.dto;

import cpdt.common.enums.DeviceType;
import cpdt.common.enums.MeasurementType;

import java.util.Map;

public class TelemetryPacket {

    private String deviceId;
    private DeviceType deviceType;
    private long timestamp;
    private Map<MeasurementType, Double> values;

    public TelemetryPacket(){}

    public TelemetryPacket(String deviceId, DeviceType deviceType, long timestamp, Map<MeasurementType, Double> values){
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.timestamp = timestamp;
        this.values = values;
    }
}