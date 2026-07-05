package cpdt.backend.entities;

import cpdt.common.enums.DeviceStatus;
import cpdt.common.enums.DeviceType;
import cpdt.common.enums.MeasurementType;
import cpdt.common.enums.ProcessArea;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Represents a telemetry measurement received from a plant device.
 *
 * <p>This entity stores telemetry published by the simulator, including
 * device information, process location, measurement type, measured value,
 * operational status, and timestamp. Each received telemetry message is
 * persisted to provide a complete historical record of plant operation.
 *
 * <p>These records serve as the primary data source for historical
 * analysis, Digital Twin synchronization, alarm evaluation, and client
 * queries.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
@Entity
@Table(
    name = "telemetry",
    indexes = {@Index(name = "idx_telemetry_device_timestamp", columnList = "deviceId,timestamp")}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelemetryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String deviceId;

    @Column(nullable = false)
    private String deviceName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceType deviceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceStatus status;

    @Column(nullable = false)
    private String locationId;

    @Column(nullable = false)
    private String locationName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProcessArea processArea;

    @Column(nullable = false)
    private Instant timestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeasurementType measurementType;

    @Column(nullable = false)
    private double value;
}