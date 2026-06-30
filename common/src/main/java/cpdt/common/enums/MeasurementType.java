package cpdt.common.enums;

import lombok.Getter;

/**
 * Enumerates every physical quantity that can be measured by the
 * simulated devices.
 *
 * <p>Each measurement type includes a display name and its associated
 * engineering unit, allowing telemetry to remain self-describing across
 * the system.
 *
 * @since 1.0
 */
@Getter
public enum MeasurementType {

    TEMPERATURE("Temperature", "°C"),
    PRESSURE("Pressure", "bar"),
    HUMIDITY("Humidity", "% RH"),
    FLOW_RATE("Flow Rate", "cubic metres/hrs"),
    LEVEL("Level", "m"),
    PH("pH", "pH"),
    VIBRATION("Vibration", "mm/s"),
    GAS_CONCENTRATION("Gas Concentration", "ppm"),
    VOLTAGE("Voltage", "V"),
    CURRENT("Current", "A");

    private final String displayName;
    private final String unit;

    MeasurementType(String displayName, String unit) {
        this.displayName = displayName;
        this.unit = unit;
    }

}