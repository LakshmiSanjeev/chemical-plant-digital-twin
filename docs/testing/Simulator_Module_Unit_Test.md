# Simulator Module Unit Test Report

**Project:** Chemical Plant Digital Twin (CPDT)  
**Module:** simulator  
**Version:** 1.0  
**Status:** Completed  
**Author:** Lakshmi Sanjeev

This document presents the unit testing activities performed for the **Simulator** module of the Chemical Plant Digital Twin (CPDT) project. The objective of unit testing is to verify that the simulator components correctly model plant environments, sensor behavior, abnormal operating scenarios, and simulation logic while producing reliable and deterministic results.

---

## 1. Scope

The scope of this document includes:

- Unit testing of environment classes
- Unit testing of the abstract sensor framework
- Verification of concrete sensor implementations
- Verification of abnormal operating scenarios
- Verification of scenario management
- Validation of simulation component behavior
- Code coverage analysis using **JaCoCo**

---

## 2. Test Environment

| Item                 | Value              |
|----------------------|--------------------|
| Programming Language | Java               |
| Java Version         | JDK 21             |
| Build Tool           | Maven              |
| Testing Framework    | JUnit 5            |
| Coverage Tool        | JaCoCo             |
| IDE                  | IntelliJ IDEA      |
| Build Command        | `mvn clean verify` |

---

## 3. Testing Strategy

The Simulator module contains the executable logic responsible for modeling the behavior of a virtual industrial chemical plant. Unit testing focused on validating the correctness of individual simulator components in isolation before integration with the Backend module.

The testing strategy included:

- Verification of constructor initialization
- Validation of normal execution paths
- Boundary condition testing
- Environment state validation
- Sensor measurement verification
- Scenario activation and recovery
- State transition verification
- Validation of simulation support classes

---

## 4. Test Scope

The following components were verified.

| Package     | Class                    | Test File          |
|-------------|--------------------------|--------------------|
| environment | PlantEnvironment         | EnvironmentTest    |
| environment | ProcessAreaEnvironment   | EnvironmentTest    |
| simulator   | SensorDevice             | SensorDeviceTest   |
| devices     | TemperatureSensor        | SensorSubclassTest |
| devices     | PressureSensor           | SensorSubclassTest |
| devices     | FlowSensor               | SensorSubclassTest |
| devices     | LevelSensor              | SensorSubclassTest |
| devices     | GasSensor                | SensorSubclassTest |
| devices     | PhSensor                 | SensorSubclassTest |
| scenarios   | Scenario                 | ScenarioTest       |
| scenarios   | FireScenario             | ScenarioTest       |
| scenarios   | GasLeakScenario          | ScenarioTest       |
| scenarios   | OverheatScenario         | ScenarioTest       |
| scenarios   | OverpressureScenario     | ScenarioTest       |
| scenarios   | EquipmentFailureScenario | ScenarioTest       |
| engine      | ScenarioEngine           | ScenarioEngineTest |

The following components were intentionally excluded from dedicated unit tests.

- Test
- TelemetryPublisher
- ConsoleTelemetryPublisher
- MqttTelemetryPublisher
- DeviceStatusSubscriber

These classes primarily perform application bootstrapping, communication, or integration with external MQTT infrastructure. Their behavior is verified through simulator execution and end-to-end integration testing rather than isolated unit tests.

---

## 5. Test Cases Executed

### 5.1 PlantEnvironment and ProcessAreaEnvironment

The environment classes were tested to verify correct initialization and management of simulated process environments.

| Test Objective                             | Result |
|--------------------------------------------|--------|
| Process area initialized correctly         | Passed |
| Default environmental values initialized   | Passed |
| Null process area rejected                 | Passed |
| Environmental values retrieved correctly   | Passed |
| Environmental values updated correctly     | Passed |
| Last updated timestamp maintained          | Passed |
| Null measurement type rejected             | Passed |
| Environment map is immutable               | Passed |
| Multiple updates retain latest value       | Passed |
| All process areas initialized              | Passed |
| Process area lookup successful             | Passed |
| Updates isolated to specified process area | Passed |

---

### 5.2 SensorDevice

The abstract sensor framework was tested to verify common sensing functionality shared by all industrial sensors.

| Test Objective                           | Result |
|------------------------------------------|--------|
| Constructor initializes fields correctly | Passed |
| Null measurement type rejected           | Passed |
| Null plant environment rejected          | Passed |
| Current value updated correctly          | Passed |
| Timestamp updated correctly              | Passed |
| Environment values retrieved correctly   | Passed |
| Hardware resolution calculated correctly | Passed |
| ADC quantization clamps below minimum    | Passed |
| ADC quantization clamps above maximum    | Passed |
| Quantized values remain within range     | Passed |
| Long-term drift executes correctly       | Passed |
| Sensor reading reflects environment      | Passed |

---

### 5.3 Sensor Implementations

Each concrete sensor implementation was verified independently.

| Sensor            | Test Coverage                                          | Result |
|-------------------|--------------------------------------------------------|--------|
| TemperatureSensor | Constructor, readings, environment response, stability | Passed |
| PressureSensor    | Constructor, readings, environment response, stability | Passed |
| FlowSensor        | Constructor, readings, environment response, stability | Passed |
| LevelSensor       | Constructor, readings, environment response, stability | Passed |
| GasSensor         | Constructor, readings, environment response, stability | Passed |
| PhSensor          | Constructor, readings, environment response, stability | Passed |

Each sensor was additionally verified to ensure:

- Correct device type assignment
- Correct measurement type assignment
- Proper initialization from environmental values
- Stable repeated readings
- Valid measurement ranges
- Response to changing environmental conditions

---

### 5.4 Scenario Classes

All implemented abnormal operating scenarios were validated.

| Scenario                 | Test Coverage                                 | Result |
|--------------------------|-----------------------------------------------|--------|
| OverheatScenario         | Temperature increase and recovery             | Passed |
| OverpressureScenario     | Pressure increase and recovery                | Passed |
| GasLeakScenario          | Gas concentration increase and recovery       | Passed |
| FireScenario             | Simultaneous temperature and gas modification | Passed |
| EquipmentFailureScenario | Sensor failure and restoration                | Passed |

Testing verified:

- Scenario activation
- Environmental modification
- Scenario deactivation
- Environment restoration
- Scenario resolution

---

### 5.5 ScenarioEngine

The ScenarioEngine was tested to verify scenario lifecycle management.

| Test Objective                       | Result |
|--------------------------------------|--------|
| Constructor rejects null environment | Passed |
| Scenario activation successful       | Passed |
| Duplicate activation prevented       | Passed |
| Scenario deactivation successful     | Passed |
| Resolved scenarios removed correctly | Passed |

---

## 6. Test Execution

All unit tests were executed using Maven.

```bash
mvn clean verify
```

Execution completed successfully.

Summary:

- Total Tests Executed: All planned unit tests
- Failed Tests: 0
- Errors: 0
- Skipped Tests: 0

All executable simulator components included within the testing scope behaved as expected.

---

## 7. Observations

### 7.1 Overall Coverage Summary

| Metric               |               Coverage |
|----------------------|-----------------------:|
| Instruction Coverage |                **67%** |
| Branch Coverage      |                **31%** |
| Classes Covered      |   **16 / 23 (69.57%)** |
| Methods Covered      |  **89 / 121 (73.55%)** |
| Lines Covered        | **506 / 831 (60.89%)** |

### 7.2 Package-wise Coverage

| Package                      | Instruction Coverage | Remarks                                                                                                                                                                               |
|------------------------------|---------------------:|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `cpdt.simulator`             |             **100%** | Core abstract sensor functionality completely verified.                                                                                                                               |
| `cpdt.simulator.scenarios`   |              **97%** | Nearly complete verification of all implemented abnormal operating scenarios.                                                                                                         |
| `cpdt.simulator.environment` |              **92%** | Environment initialization and update behavior extensively tested.                                                                                                                    |
| `cpdt.simulator.devices`     |              **89%** | All concrete sensor implementations validated across multiple operating conditions.                                                                                                   |
| `cpdt.simulator.engine`      |              **26%** | Core scenario management verified. Remaining uncovered instructions primarily correspond to simulation orchestration and publisher interactions exercised during integration testing. |
| `cpdt.simulator.bootstrap`   |               **0%** | Bootstrap class contains application startup logic and was intentionally excluded from unit testing.                                                                                  |
| `cpdt.simulator.mqtt`        |               **0%** | MQTT communication components are validated through integration testing rather than isolated unit tests.                                                                              |

The following observations were made during testing.

- All environment classes initialized and updated correctly.
- Sensor measurements consistently reflected environmental changes.
- Sensor readings remained within configured operating ranges.
- ADC quantization and hardware resolution behaved as expected.
- Scenario activation and recovery modified environmental values correctly.
- Equipment failure simulation correctly altered and restored sensor state.
- Scenario lifecycle management functioned as expected.
- No unexpected runtime exceptions were encountered.
- All planned unit tests completed successfully.

---

## 8. Limitations

This testing phase focuses exclusively on isolated unit testing of the Simulator module.

The following activities are covered in separate testing phases.

- MQTT communication testing
- Backend integration testing
- PostgreSQL persistence testing
- REST API testing
- End-to-end Digital Twin testing
- Performance and stress testing
- Long-duration simulation testing

---

## Appendix A – Test Commands

Run all tests.

```bash
mvn clean test
```

Generate coverage report.

```bash
mvn clean verify
```

Generate Javadocs.

```bash
mvn javadoc:aggregate
```

---

## Appendix B – Generated Artifacts

The following artifacts were generated during verification.

- JUnit Test Reports
- JaCoCo HTML Coverage Report
- Maven Surefire Reports
- Aggregated Project Javadocs

These artifacts provide supporting evidence for successful verification of the Simulator module.