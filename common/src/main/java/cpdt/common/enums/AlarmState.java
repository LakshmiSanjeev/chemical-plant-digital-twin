package cpdt.common.enums;

/**
 * Represents the state transition of a process alarm after evaluating
 * newly received telemetry.
 *
 * <p>This enumeration is used by the backend alarm evaluation logic to
 * determine whether an alarm has been raised, escalated, de-escalated,
 * cleared, or remains unchanged.
 *
 * @since 1.0
 */
public enum AlarmState {
    NEW_ALARM,
    ESCALATED,
    DEESCALATED,
    CLEARED,
    NO_CHANGE
}
