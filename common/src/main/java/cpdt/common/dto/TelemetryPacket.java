package cpdt.common.dto;

import cpdt.common.enums.DeviceType;
import cpdt.common.enums.MeasurementType;

import java.util.Map;

public class TelemetryPacket {

    private String deviceId;
    private DeviceType deviceType;

    private long timestamp;

    private Map<MeasurementType, Double> values;

}