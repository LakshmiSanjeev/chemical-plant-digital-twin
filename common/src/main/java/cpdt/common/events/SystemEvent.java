package cpdt.common.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class SystemEvent {
    private String eventId;
    private long timestamp;
    private String sourceDeviceId;
    private String message;
}