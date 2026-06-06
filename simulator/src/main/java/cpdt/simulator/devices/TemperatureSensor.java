package cpdt.simulator.devices;

import cpdt.common.enums.DeviceType;
import cpdt.common.enums.MeasurementType;
import cpdt.common.enums.ProcessArea;
import cpdt.common.models.Location;
import cpdt.simulator.SensorDevice;
import cpdt.simulator.environment.PlantEnvironment;

public class TemperatureSensor extends SensorDevice {

    public TemperatureSensor(
            String deviceId,
            String name,
            Location location,
            PlantEnvironment plantEnvironment,
            ProcessArea processArea
    ) {

        super(
                deviceId,
                name,
                DeviceType.TEMPERATURE_SENSOR,
                location,
                MeasurementType.TEMPERATURE,
                plantEnvironment,
                processArea
        );
    }

    @Override
    public double getReading() {

        double actualTemperature = getEnvironmentValue();

        setCurrentValue(actualTemperature);

        return actualTemperature;
    }
}