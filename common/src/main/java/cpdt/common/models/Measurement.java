package cpdt.common.models;

import cpdt.common.enums.MeasurementType;

public class Measurement {

    private MeasurementType type;
    private double value;
    private String unit;

    public Measurement(){}

    public Measurement(MeasurementType type, double value, String unit){
        this.type = type;
        this.value = value;
        this.unit = unit;
    }
}
