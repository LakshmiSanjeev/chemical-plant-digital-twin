/*
Contains all the process area environments and orchestrates them.
 */

package cpdt.simulator.environment;

import cpdt.common.enums.MeasurementType;
import cpdt.common.enums.ProcessArea;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class PlantEnvironment {

    private final Map<ProcessArea, ProcessAreaEnvironment> processAreaEnvironments;

    public PlantEnvironment() {
        this.processAreaEnvironments = new EnumMap<>(ProcessArea.class);
        initializeProcessAreas();
    }

    private void initializeProcessAreas() {
        for (ProcessArea processArea : ProcessArea.values()) {
            processAreaEnvironments.put(processArea, new ProcessAreaEnvironment(processArea));
        }
    }

    public double getValue(ProcessArea processArea, MeasurementType measurementType) {
        return getProcessAreaEnvironment(processArea).getEnvironmentalValue(measurementType);
    }

    public void setValue(ProcessArea processArea, MeasurementType measurementType, double value) {
        getProcessAreaEnvironment(processArea).setEnvironmentalValue(measurementType, value);
    }

    public ProcessAreaEnvironment getProcessAreaEnvironment(ProcessArea processArea) {
        Objects.requireNonNull(processArea, "ProcessArea cannot be null");
        ProcessAreaEnvironment environment = processAreaEnvironments.get(processArea);
        if (environment == null) {
            throw new IllegalArgumentException("No environment found for process area: " + processArea);
        }
        return environment;
    }

    public Map<ProcessArea, ProcessAreaEnvironment> getAllEnvironments() {
        return Collections.unmodifiableMap(processAreaEnvironments);
    }
}