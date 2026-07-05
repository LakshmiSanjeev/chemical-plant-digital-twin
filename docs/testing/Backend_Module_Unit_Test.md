# Backend Module Unit Test Report

**Project:** Chemical Plant Digital Twin (CPDT)  
**Module:** backend  
**Version:** 1.0  
**Status:** Completed  
**Author:** Lakshmi Sanjeev  

This document presents the unit testing activities performed for the **Backend** module of the Chemical Plant Digital Twin (CPDT) project. The objective of unit testing is to verify that the Backend correctly performs telemetry ingestion, validation, persistence, alarm evaluation, Digital Twin state management, REST API processing, and device status management while producing reliable and deterministic results.

---

## 1. Scope

The scope of this document includes:

* Unit testing of telemetry ingestion and validation
* Verification of telemetry and device persistence
* Verification of alarm evaluation and alarm state management
* Validation of alert persistence and retrieval
* Verification of Digital Twin state management
* Verification of REST controllers
* Validation of MQTT configuration and message handling
* Spring Boot application context verification
* Code coverage analysis using **JaCoCo**

---

## 2. Test Environment

| Item                 | Value              |
|----------------------|--------------------|
| Programming Language | Java               |
| Java Version         | JDK 21             |
| Build Tool           | Maven              |
| Testing Framework    | JUnit 5            |
| Mocking Framework    | Mockito            |
| REST Testing         | Spring MockMvc     |
| Coverage Tool        | JaCoCo             |
| IDE                  | IntelliJ IDEA      |
| Build Command        | `mvn clean verify` |

---

## 3. Testing Strategy

The Backend module contains the primary business logic of the Chemical Plant Digital Twin platform. Unit testing focused on validating individual components in isolation while mocking external dependencies such as repositories, MQTT communication, and persistence infrastructure.

The testing strategy included:

* Verification of normal execution paths
* Validation of invalid input handling
* Boundary condition testing
* Dependency interaction verification
* Repository interaction verification
* REST endpoint verification
* Exception handling validation
* Digital Twin state verification
* Configuration validation

---

## 4. Test Design Approach

The Backend module was tested primarily using isolated unit tests with Mockito-based dependency mocking and Spring Boot testing support.

Business services were tested independently by mocking repositories, MQTT publishers, and dependent services. REST controllers were verified using Spring `@WebMvcTest` with MockMvc, allowing endpoint behavior to be validated without starting the complete application.

Application configuration and Spring Boot initialization were verified separately through dedicated configuration and context-loading tests.

---

## 5. Test Scope

The following components were verified.

| Package     | Class                       | Test File                   |
|-------------|-----------------------------|-----------------------------|
| services    | TelemetryIngestionService   | TelemetryIngestionTest      |
| services    | TelemetryPersistenceService | TelemetryPersistenceTest    |
| services    | DevicePersistenceService    | DevicePersistenceTest       |
| services    | AlertEvaluationService      | AlarmEvaluationTest         |
| services    | AlarmStateService           | AlarmStateServiceTest       |
| services    | AlertPersistenceService     | AlertPersistenceTest        |
| services    | AlertFetch                  | AlertFetchTest              |
| services    | DeviceStatusService         | DeviceStatusServiceTest     |
| services    | TelemetryQueryService       | TelemetryQueryServiceTest   |
| mqtt        | TelemetryMessageHandler     | TelemetryMessageHandlerTest |
| config      | MqttIngestionConfig         | MqttIngestionTest           |
| twin        | TwinStateStore              | TwinStateStoreTest          |
| controllers | AlertController             | AlertControllerTest         |
| controllers | DeviceController            | DeviceControllerTest        |
| controllers | TelemetryController         | TelemetryControllerTest     |
| controllers | TwinController              | TwinControllerTest          |
| backend     | BackendApplication          | BackendApplicationTests     |

The following components were intentionally excluded from dedicated unit tests.

* Repository interfaces
* Entity classes
* Exception DTOs
* Spring-generated framework components

These classes primarily consist of persistence mappings, framework-managed behavior, or simple data containers. Their functionality is indirectly verified through service and controller tests.

---

## 6. Test Cases Executed

### 6.1 TelemetryIngestionService

| Test Objective                               | Result |
|----------------------------------------------|--------|
| Valid telemetry packet ingested successfully | Passed |
| Device registration executed                 | Passed |
| Telemetry persistence executed               | Passed |
| Digital Twin state updated                   | Passed |
| Alert persisted when generated               | Passed |
| Null payload rejected                        | Passed |
| Empty payload rejected                       | Passed |
| Invalid JSON payload rejected                | Passed |
| Missing device ID rejected                   | Passed |
| Missing device name rejected                 | Passed |
| Missing device type rejected                 | Passed |
| Missing device status rejected               | Passed |
| Missing process area rejected                | Passed |
| Missing measurement type rejected            | Passed |
| Missing location information rejected        | Passed |
| Invalid timestamp rejected                   | Passed |
| NaN measurement values rejected              | Passed |
| Null telemetry packet rejected               | Passed |

### 6.2 TelemetryPersistenceService

| Test Objective                        | Result |
|---------------------------------------|--------|
| Telemetry entity created successfully | Passed |
| Telemetry fields mapped correctly     | Passed |
| Timestamp converted correctly         | Passed |
| Repository save invoked               | Passed |

### 6.3 DevicePersistenceService

| Test Objective                    | Result |
|-----------------------------------|--------|
| New device created successfully   | Passed |
| Existing device updated correctly | Passed |
| Device retrieval by ID verified   | Passed |
| Retrieval of all devices verified | Passed |

### 6.4 AlertEvaluationService

| Test Objective                             | Result |
|--------------------------------------------|--------|
| Missing alarm thresholds handled correctly | Passed |
| Disabled thresholds ignored                | Passed |
| Normal measurements clear active alarms    | Passed |
| Warning High alert generated               | Passed |
| Warning Low alert generated                | Passed |
| Critical High alert generated              | Passed |
| Critical Low alert generated               | Passed |
| Duplicate alarm conditions suppressed      | Passed |
| New alarm creation verified                | Passed |

### 6.5 AlarmStateService

| Test Objective                             | Result |
|--------------------------------------------|--------|
| New alarm created successfully             | Passed |
| Alarm escalation verified                  | Passed |
| Alarm de-escalation verified               | Passed |
| Duplicate alarm state detected             | Passed |
| Alarm cleared successfully                 | Passed |
| Clearing inactive alarms handled correctly | Passed |
| Active alarm lookup verified               | Passed |
| Unknown device handling verified           | Passed |

### 6.6 AlertPersistenceService

| Test Objective                 | Result |
|--------------------------------|--------|
| Alert entity created correctly | Passed |
| Alert fields mapped correctly  | Passed |
| Repository save verified       | Passed |

### 6.7 AlertFetch

| Test Objective                   | Result |
|----------------------------------|--------|
| Retrieve paginated alerts        | Passed |
| Retrieve active alerts           | Passed |
| Alert acknowledgement verified   | Passed |
| Missing alert detection verified | Passed |

### 6.8 DeviceStatusService

| Test Objective                   | Result |
|----------------------------------|--------|
| Missing device detection         | Passed |
| Duplicate status update ignored  | Passed |
| Device status updated correctly  | Passed |
| MQTT status publication verified | Passed |

### 6.9 TelemetryQueryService

| Test Objective                           | Result |
|------------------------------------------|--------|
| Device telemetry history retrieved       | Passed |
| Process area telemetry history retrieved | Passed |
| Repository query interaction verified    | Passed |

### 6.10 MQTT Components

#### TelemetryMessageHandler

| Test Objective                           | Result |
|------------------------------------------|--------|
| Telemetry delegated to ingestion service | Passed |
| Invalid telemetry handled safely         | Passed |
| Unexpected runtime exceptions handled    | Passed |

#### MQTT Configuration

| Test Objective                | Result |
|-------------------------------|--------|
| MQTT client factory created   | Passed |
| MQTT input channel created    | Passed |
| MQTT output channel created   | Passed |
| MQTT inbound adapter created  | Passed |
| MQTT outbound handler created | Passed |

### 6.11 Digital Twin State Management

| Test Objective                   | Result |
|----------------------------------|--------|
| New Digital Twin state created   | Passed |
| Existing state updated           | Passed |
| Device lookup verified           | Passed |
| Retrieval of all device states   | Passed |
| Retrieval by process area        | Passed |
| Unknown device handling verified | Passed |
| Empty process area handled       | Passed |

### 6.12 REST Controllers

#### AlertController

* Retrieval of paginated alerts
* Retrieval of active alerts
* Alert acknowledgement endpoint
* Empty response handling

**Result:** Passed

#### DeviceController

* Retrieval of all devices
* Retrieval by device ID
* Empty device list handling
* HTTP 404 validation

**Result:** Passed

#### TelemetryController

* Device telemetry history
* Process area telemetry history
* Default request parameters
* Invalid request validation
* HTTP 400 validation

**Result:** Passed

#### TwinController

* Retrieval of all Digital Twin states
* Device state retrieval
* Process area filtering
* HTTP 404 validation
* HTTP 400 validation

**Result:** Passed

### 6.13 Spring Boot Context

The application context was successfully initialized using Spring Boot integration testing.

| Test Objective                                | Result |
|-----------------------------------------------|--------|
| Spring application context loads successfully | Passed |

---

## 7. Test Execution

All unit tests were executed using Maven.

```bash
mvn clean verify
```

Execution completed successfully.

Summary:

* Total Tests Executed: All planned unit tests
* Failed Tests: 0
* Errors: 0
* Skipped Tests: 0

All executable Backend components included within the testing scope behaved as expected.

---

## 8. Coverage Analysis

### 8.1 Overall Coverage Summary

| Metric               |               Coverage |
|----------------------|-----------------------:|
| Instruction Coverage |                **92%** |
| Branch Coverage      |                **87%** |
| Classes Covered      |     **21 / 21 (100%)** |
| Methods Covered      |   **61 / 66 (92.42%)** |
| Lines Covered        | **258 / 271 (95.20%)** |

### 8.2 Package-wise Coverage

| Package                    | Instruction Coverage | Remarks                                                                                                                                                 |
|----------------------------|---------------------:|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| `cpdt.backend.services`    |              **99%** | Nearly complete verification of telemetry ingestion, persistence, alarm evaluation, alert management, and device status processing.                     |
| `cpdt.backend.config`      |             **100%** | MQTT configuration classes completely verified.                                                                                                         |
| `cpdt.backend.controllers` |             **100%** | REST API endpoints comprehensively validated using MockMvc.                                                                                             |
| `cpdt.backend.twin`        |             **100%** | Digital Twin state management completely verified.                                                                                                      |
| `cpdt.backend.mqtt`        |              **64%** | Message handling validated. Remaining instructions correspond primarily to framework-managed MQTT execution paths exercised during integration testing. |
| `cpdt.backend.exception`   |              **64%** | Exception handling verified. Remaining instructions correspond to framework-managed execution paths.                                                    |
| `cpdt.backend`             |              **37%** | Spring Boot bootstrap class verified through application context loading. Remaining instructions correspond to framework startup code.                  |

---

## 9. Observations

The following observations were made during testing.

* Telemetry validation correctly rejected malformed and incomplete messages.
* Device registration and persistence behaved consistently across repeated executions.
* Alarm evaluation correctly detected warning and critical threshold violations.
* Alarm state transitions, including escalation, de-escalation, and clearing, behaved as expected.
* Alert persistence accurately converted DTOs into database entities.
* Digital Twin state remained synchronized with incoming telemetry updates.
* REST controllers returned the expected HTTP responses for valid, invalid, and boundary conditions.
* MQTT message handling delegated processing correctly while safely handling invalid telemetry.
* Spring configuration components initialized successfully.
* No unexpected runtime exceptions were encountered.
* All planned unit tests completed successfully.

---

## 10. Limitations

This testing phase focuses exclusively on isolated unit testing of the Backend module.

The following activities are covered in separate testing phases.

* PostgreSQL integration testing
* MQTT communication testing
* End-to-end Digital Twin testing
* Simulator integration testing
* REST API integration testing
* Performance testing
* Stress testing
* Long-duration system testing

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

* JUnit Test Reports
* JaCoCo HTML Coverage Report
* Maven Surefire Reports
* Spring Boot Test Reports
* Aggregated Project Javadocs

These artifacts provide supporting evidence for successful verification of the Backend module.