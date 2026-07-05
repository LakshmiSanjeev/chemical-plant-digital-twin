package cpdt.backend.entities;

import cpdt.common.enums.MeasurementType;
import cpdt.common.enums.ProcessArea;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents alarm threshold configuration for a process measurement.
 *
 * <p>This entity stores the warning and critical threshold values used
 * by the Digital Twin backend to evaluate incoming telemetry. A unique
 * threshold configuration exists for each combination of process area
 * and measurement type, allowing alarm evaluation rules to be configured
 * independently across the plant.
 *
 * <p>The stored threshold values include hysteresis settings and an
 * enable flag to support configurable alarm processing.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
@Entity
@Table(
    name = "alarm_thresholds",
    uniqueConstraints = {@UniqueConstraint(name = "unique_key_area_measurement", columnNames = {"process_area","measurement_type"})}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmThresholdEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "process_area", nullable = false)
    private ProcessArea processArea;

    @Enumerated(EnumType.STRING)
    @Column(name = "measurement_type", nullable = false)
    private MeasurementType measurementType;

    @Column(name = "warning_low_threshold", nullable = false)
    private double warningLowThreshold;

    @Column(name = "critical_low_threshold", nullable = false)
    private double criticalLowThreshold;

    @Column(name = "warning_high_threshold", nullable = false)
    private double warningHighThreshold;

    @Column(name = "critical_high_threshold", nullable = false)
    private double criticalHighThreshold;

    @Column(name = "hysteresis", nullable = false)
    private double hysteresis;

    @Column(nullable = false)
    private boolean enabled = true;
}