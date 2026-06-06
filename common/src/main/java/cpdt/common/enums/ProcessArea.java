package cpdt.common.enums;

public enum ProcessArea {
    REACTOR_SECTION(45.0, 1.05, 30.0, 8.0),
    STORAGE_SECTION(32.0, 1.01, 50.0, 1.0),
    FEED_SECTION(38.0, 1.03, 40.0, 3.0),
    DISTILLATION_SECTION(55.0, 1.04, 25.0, 5.0),
    COOLING_SECTION(28.0, 1.01, 70.0, 0.0),
    UTILITIES_SECTION(35.0, 1.02, 45.0, 1.0),
    PIPELINE_SECTION(40.0, 1.02, 35.0, 2.0);

    private final double temperature;
    private final double pressure;
    private final double humidity;
    private final double gasConcentration;

    ProcessArea(double temp, double press, double hum, double gas) {
        this.temperature = temp;
        this.pressure = press;
        this.humidity = hum;
        this.gasConcentration = gas;
    }

    public double getDefaultValue(MeasurementType type) {
        return switch (type) {
            case TEMPERATURE -> temperature;
            case PRESSURE -> pressure;
            case HUMIDITY -> humidity;
            case GAS_CONCENTRATION -> gasConcentration;
            default -> throw new IllegalArgumentException("Unsupported environmental measurement type: " + type);
        };
    }
}
