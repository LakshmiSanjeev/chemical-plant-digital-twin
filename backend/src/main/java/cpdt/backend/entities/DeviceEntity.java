package cpdt.backend.entities;

import cpdt.common.enums.DeviceStatus;
import cpdt.common.enums.DeviceType;
import cpdt.common.enums.ProcessArea;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Represents a registered device within the Digital Twin backend.
 *
 * <p>This entity stores static device information together with its
 * current operational status. It serves as the backend's persistent
 * device registry and is updated as telemetry is received and device
 * state changes occur during system operation.
 *
 * <p>The stored information enables clients to query the latest known
 * status and metadata for each simulated plant device.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
@Entity
@Table(name = "devices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private String deviceId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceType deviceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProcessArea processArea;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceStatus status;

    @Column(nullable = false)
    private Instant lastUpdated;
}