package cpdt.common.enums;

import lombok.Getter;

@Getter
public enum MeasurementType {

    TEMPERATURE("Temperature", "°C"),
    PRESSURE("Pressure", "bar"),
    HUMIDITY("Humidity", "% RH"),
    FLOW_RATE("Flow Rate", "L/min"),
    FILL_LEVEL("Level", "%"),
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