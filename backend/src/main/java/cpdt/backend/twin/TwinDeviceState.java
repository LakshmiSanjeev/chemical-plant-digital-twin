package cpdt.backend.twin;

import cpdt.common.enums.DeviceStatus;
import cpdt.common.enums.MeasurementType;
import cpdt.common.enums.ProcessArea;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Represents the current Digital Twin state of a device.
 *
 * <p>This class stores the latest known operational information for a
 * device, including its most recent measurement, operational status,
 * measurement type, process area, and update timestamp. It provides
 * the in-memory representation of a device within the Digital Twin.
 *
 * <p>Instances of this class are maintained by the Digital Twin state
 * store and updated whenever new telemetry is received.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
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