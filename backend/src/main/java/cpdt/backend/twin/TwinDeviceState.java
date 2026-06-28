package cpdt.backend.twin;

import cpdt.common.enums.DeviceStatus;
import cpdt.common.enums.MeasurementType;
import cpdt.common.enums.ProcessArea;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@RequiredArgsConstructor
public class TwinDeviceState {

    private final String deviceId;

    private double latestValue;

    private DeviceStatus latestStatus;

    private MeasurementType measurementType;

    private ProcessArea processArea;

    private Instant lastUpdated;
}