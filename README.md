# Chemical Plant Digital Twin

*A modular Industrial IoT Digital Twin platform for real-time chemical
plant simulation, MQTT telemetry ingestion, alarm management, digital
twin state synchronization, and industrial monitoring.*

[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)]()
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?logo=springboot)]()
[![Maven](https://img.shields.io/badge/Maven-C71A36?logo=apachemaven)]()
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?logo=postgresql)]()
[![MQTT](https://img.shields.io/badge/MQTT-Mosquitto-660066?logo=eclipsemosquitto)]()
[![JUnit5](https://img.shields.io/badge/JUnit-5-25A162?logo=junit5)]()
[![JaCoCo](https://img.shields.io/badge/JaCoCo-Code_Coverage-brightgreen)]()

------------------------------------------------------------------------

## Overview

Chemical Plant Digital Twin (CPDT) is a modular Java application that
models an industrial chemical plant through a software Digital Twin. The
project simulates plant devices, publishes telemetry over MQTT, ingests
and validates telemetry using Spring Boot, evaluates alarms with
hysteresis, maintains a live digital twin state, stores historical
telemetry in PostgreSQL, and exposes REST APIs for monitoring.

------------------------------------------------------------------------

## Live Documentation

-   **GitHub Repository**
    https://github.com/LakshmiSanjeev/chemical-plant-digital-twin

-   **REST API (Postman)**
    https://documenter.getpostman.com/view/56225642/2sBY4HV4u6

-   **Published Javadocs**
    https://lakshmisanjeev.github.io/chemical-plant-digital-twin/javadocs/apidocs/index.html

------------------------------------------------------------------------

## Features

### Simulator

-   Chemical plant simulation
-   Multiple industrial sensor models
-   Failure scenarios
-   MQTT telemetry publishing

### Backend

-   Spring Boot REST API
-   MQTT ingestion
-   Telemetry validation
-   PostgreSQL persistence
-   Alarm threshold evaluation
-   Alarm hysteresis
-   Alert lifecycle management
-   Device state management
-   Live Digital Twin state

### Common Module

-   Shared DTOs
-   Models
-   Enums
-   Serialization utilities
-   Topic builders

------------------------------------------------------------------------

## Architecture

``` text
               Chemical Plant
                     │
                     ▼
               Simulator Module
                     │
                MQTT Telemetry
                     │
               Mosquitto Broker
                     │
             Spring Boot Backend
                     │
     ┌───────────────┼────────────────┐
     │               │                │
 Validation      PostgreSQL      Digital Twin
     │               │                │
     └───────────────┼────────────────┘
                     │
                 REST APIs
                     │
             Dashboard / Client
```

------------------------------------------------------------------------

## Technology Stack

| Layer       | Technology             |
|-------------|------------------------|
| Language    | Java 21                |
| Framework   | Spring Boot            |
| Build       | Maven                  |
| Database    | PostgreSQL             |
| Messaging   | Eclipse Mosquitto MQTT |
| Testing     | JUnit 5                |
| Coverage    | JaCoCo                 |
| API Testing | Postman                |

------------------------------------------------------------------------

## Repository Structure

``` text
chemical-plant-digital-twin
├── simulator
├── backend
├── common
├── docs
└── pom.xml
```

------------------------------------------------------------------------

## Getting Started

### Prerequisites

-   Java 21
-   Maven
-   PostgreSQL
-   Eclipse Mosquitto

### Clone

``` bash
git clone https://github.com/LakshmiSanjeev/chemical-plant-digital-twin.git
cd chemical-plant-digital-twin
```

### Build

``` bash
mvn clean install
```

### Run Backend

``` bash
cd backend
mvn spring-boot:run
```

### Run Simulator

``` bash
cd simulator
mvn exec:java
```

------------------------------------------------------------------------

## Testing

``` bash
mvn test
mvn verify
```

Testing includes: 
- Unit Tests
- Backend Integration Tests 
- JaCoCo Coverage

------------------------------------------------------------------------

## Documentation

-   Backend Design
-   Simulator Design
-   Common Module Design
-   Unit Testing Documentation
-   JaCoCo Reports
-   Published Javadocs
-   Published Postman Collection

------------------------------------------------------------------------

## Future Work

-   Web dashboard
-   Historical trend visualization
-   Predictive maintenance
-   Docker deployment
-   Kubernetes deployment
-   Authentication
-   Streaming analytics

------------------------------------------------------------------------

## License

MIT License.

------------------------------------------------------------------------

## Author

**Lakshmi Sanjeev**

Electronics & Communication Engineering

GitHub: https://github.com/LakshmiSanjeev
