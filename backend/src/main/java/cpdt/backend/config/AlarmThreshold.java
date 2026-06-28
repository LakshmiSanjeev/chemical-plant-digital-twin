package cpdt.backend.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlarmThreshold {
    private double warningLowThreshold;
    private double criticalLowThreshold;
    private double warningHighThreshold;
    private double criticalHighThreshold;
}