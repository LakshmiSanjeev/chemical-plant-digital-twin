# Common Module Unit Test Report

**Project:** Chemical Plant Digital Twin (CPDT)  
**Module:** common  
**Version:** 1.0  
**Status:** Completed  
**Author:** Lakshmi Sanjeev

This document presents the unit testing activities performed for the **Common** module of the Chemical Plant Digital Twin (CPDT) project. The objective of unit testing is to verify that all executable components within the module behave correctly under expected operating conditions and produce reliable, deterministic results.

---

## 1. Scope

The scope of this document includes:

- Unit testing of utility classes
- Validation of enumeration definitions
- Verification of serialization utilities
- Verification of MQTT topic generation
- Verification of unique device ID generation
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

The Common module primarily contains reusable components that support the Simulator and Backend modules. Testing focused on executable logic rather than passive data structures.

The testing strategy included:

- Verification of normal execution paths
- Validation of expected outputs
- Boundary condition testing where applicable
- Validation of enum values
- Serialization correctness
- Utility method verification

Passive DTOs, Java records, and immutable data classes were not unit tested separately because they contain no business logic beyond compiler-generated constructors and accessors.

---

## 4. Test Scope

The following components were verified.

| Package | Class               | Test File               |
|---------|---------------------|-------------------------|
| utils   | DeviceIdGenerator   | DeviceIdGeneratorTest   |
| utils   | TopicBuilder        | TopicBuilderTest        |
| utils   | TelemetrySerializer | TelemetrySerializerTest |
| enums   | AlarmState          | EnumsTest               |
| enums   | AlertSeverity       | EnumsTest               |
| enums   | DeviceStatus        | EnumsTest               |
| enums   | DeviceType          | EnumsTest               |
| enums   | MeasurementType     | EnumsTest               |
| enums   | ProcessArea         | EnumsTest               |

The following classes were intentionally excluded from dedicated unit tests because they are passive data carriers without executable business logic.

- Device
- Location
- TelemetryPacket
- AlertMessage
- DeviceStatusMessage

These classes primarily consist of immutable fields, constructors, getters, setters, or Java records. Their functionality is implicitly verified through higher-level tests that exercise serialization and utility methods.

---

## 5. Test Cases Executed

### 5.1 DeviceIdGenerator

| Test Objective                                       | Result |
|------------------------------------------------------|--------|
| Generated ID is not null                             | Passed |
| Generated ID is not empty                            | Passed |
| Generated IDs are unique across multiple generations | Passed |
| Generated IDs conform to the expected format         | Passed |


### 5.2 TopicBuilder

The TopicBuilder utility was tested to verify correct MQTT topic construction.

| Test Objective                                   | Result |
|--------------------------------------------------|--------|
| Topic generated successfully                     | Passed |
| Topic includes the correct process area          | Passed |
| Topic includes the correct device identifier     | Passed |
| Topic format matches the expected MQTT hierarchy | Passed |


### 5.3 TelemetrySerializer

The serializer was tested using representative telemetry packets.

| Test Objective                                           | Result |
|----------------------------------------------------------|--------|
| Serialization completes without exceptions               | Passed |
| Serialized output is not null                            | Passed |
| Required telemetry fields are present in the JSON output | Passed |
| Output is valid JSON                                     | Passed |


### 5.4 Enumeration Validation

All enumeration classes were validated.

| Enumeration     | Test Coverage                                                          | Result |
|-----------------|------------------------------------------------------------------------|--------|
| AlarmState      | Constant definitions and `valueOf()`                                   | Passed |
| AlertSeverity   | Constant definitions and `valueOf()`                                   | Passed |
| DeviceStatus    | Constant definitions and `valueOf()`                                   | Passed |
| DeviceType      | Constant definitions and `valueOf()`                                   | Passed |
| MeasurementType | Constant definitions and `valueOf()`                                   | Passed |
| ProcessArea     | Constant definitions, associated measurement mappings, and `valueOf()` | Passed |


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

All executable components included within the test scope behaved as expected.

---

## 7. Observations

### 7.1 Overall Coverage Summary

| Metric               |               Coverage |
|----------------------|-----------------------:|
| Instruction Coverage |                **87%** |
| Branch Coverage      |               **100%** |
| Classes Covered      |   **10 / 14 (71.43%)** |
| Methods Covered      |   **21 / 28 (75.00%)** |
| Lines Covered        | **136 / 157 (86.62%)** |

### 7.2 Package-wise Coverage

| Package              | Instruction Coverage | Remarks                                                                                                                                                   |
|----------------------|---------------------:|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| `cpdt.common.enums`  |             **100%** | All enumeration classes were completely verified through unit tests.                                                                                      |
| `cpdt.common.utils`  |              **73%** | Utility classes were extensively tested. Remaining uncovered instructions correspond to exceptional or auxiliary code paths.                              |
| `cpdt.common.dto`    |              **50%** | DTOs contain minimal executable logic and were indirectly validated through serializer tests.                                                             |
| `cpdt.common.models` |               **0%** | Domain model classes were intentionally excluded from dedicated unit tests as they primarily consist of immutable data structures without business logic. |

The following observations were made during testing.

- All utility classes behaved as expected.
- Device ID generation consistently produced valid unique identifiers.
- MQTT topic generation followed the expected hierarchy and naming convention.
- Telemetry serialization produced valid JSON output.
- Enumeration values matched the project specifications.
- No runtime exceptions were encountered during execution.
- All planned unit tests completed successfully.

---

## 8. Limitations

This testing phase focuses exclusively on isolated unit testing of the Common module.

The following activities are covered in separate testing phases.

- Backend integration testing
- Simulator testing
- MQTT communication testing
- PostgreSQL persistence testing
- REST API testing
- End-to-end system testing
- Performance testing


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

These artifacts provide supporting evidence for successful verification of the Common module.