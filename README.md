# Smart Campus API: Room & Instrument Management

> **Module: 5COSC022W — Client-Server Architectures (Academic Year 2025/26)**  
> **Institution: University of Westminster — Computer Science & Engineering**

## Project Introduction

This project provides a robust RESTful backend for managing a smart campus environment. Built using **Java 11** and the **Jersey JAX-RS** framework, it features a high-performance in-memory data store and runs on a lightweight, embedded **Grizzly server**. The API supports full lifecycle management of campus rooms and sensors, complete with historical data tracking and sophisticated error handling.

---

## Documentation Navigation

- [Technical Summary](#technical-summary)
- [System Architecture](#system-architecture)
- [Installation & Setup](#installation--setup)
- [API Services Reference](#api-services-reference)
- [Demonstration Commands](#demonstration-commands)
- [Architecture Report & Analysis](#architecture-report--analysis)

---

## Technical Summary

| Component | Implementation Details |
| :--- | :--- |
| **Runtime** | Java 11 (LTS) |
| **Framework** | Jersey 2.41 (JAX-RS) |
| **Server** | Grizzly HTTP Server |
| **JSON Provider** | Jackson Serialization |
| **Dependency Management** | Apache Maven |
| **Persistence** | In-memory `ConcurrentHashMap` & `ArrayList` |

---

## System Architecture

The codebase is organized into logical packages to ensure separation of concerns:

```text
smart-campus-api/
├── pom.xml
└── src/main/java/com/smartcampus/
    ├── application/
    │   ├── Main.java                        # System bootstrap and server configuration
    │   ├── SmartCampusApplication.java      # JAX-RS root path definition (/api/v1)
    │   └── DataStore.java                   # Thread-safe singleton data repository
    ├── model/
    │   ├── Room.java                        # Room entity definition
    │   ├── Sensor.java                      # Sensor/Instrument entity definition
    │   └── SensorReading.java               # Measurement record definition
    ├── resource/
    │   ├── DiscoveryResource.java           # API entry point & metadata provider
    │   ├── RoomResource.java                # Room management services
    │   ├── SensorResource.java              # Instrument services & sub-resource routing
    │   └── SensorReadingResource.java       # Historical data services (nested)
    ├── exception/
    │   ├── RoomNotEmptyException.java       # Integrity violation exception
    │   ├── LinkedResourceNotFoundException.java # Reference validation exception
    │   ├── SensorUnavailableException.java  # State-based access exception
    │   └── *Mapper.java                     # Custom HTTP response translators
    └── filter/
        └── LoggingFilter.java               # Network traffic observability
```

---

## Installation & Setup

### Prerequisites

Ensure your development environment has:
- **Java 11 or higher**
- **Maven 3.6 or higher**

### Deployment Steps

1. **Get the Source:**
   ```bash
   git clone https://github.com/Sakindu17/smart-campus-API.git
   cd smart-campus-api
   ```

2. **Build the Application:**
   ```bash
   mvn clean package
   ```
   *This will generate a "fat" executable JAR in the `target/` directory.*

3. **Start the Backend:**
   ```bash
   java -jar target/smart-campus-api-1.0-SNAPSHOT.jar
   ```

4. **Verify Connectivity:**
   ```bash
   curl http://localhost:8080/api/v1
   ```

---

## API Services Reference

### Room Services

| Action | Route | Expected Status |
| :--- | :--- | :--- |
| List All | `GET /api/v1/rooms` | 200 OK |
| Create | `POST /api/v1/rooms` | 201 Created |
| Get Details | `GET /api/v1/rooms/{roomId}` | 200 OK |
| Delete | `DELETE /api/v1/rooms/{roomId}` | 204 No Content |

### Instrument Services

| Action | Route | Expected Status |
| :--- | :--- | :--- |
| List All | `GET /api/v1/sensors` | 200 OK |
| Filter by Type | `GET /api/v1/sensors?type={type}` | 200 OK |
| Register | `POST /api/v1/sensors` | 201 Created |
| Get Details | `GET /api/v1/sensors/{sensorId}` | 200 OK |

### Data Measurement Services (Nested)

| Action | Route | Expected Status |
| :--- | :--- | :--- |
| View History | `GET /api/v1/sensors/{id}/readings` | 200 OK |
| Record Data | `POST /api/v1/sensors/{id}/readings` | 201 Created |

### Status Code Mapping

| Code | Meaning |
| :--- | :--- |
| **400** | Request body is missing required information |
| **403** | Forbidden: Instrument is under maintenance |
| **404** | The requested resource does not exist |
| **409** | Conflict: Cannot delete room while sensors are present |
| **422** | Invalid Reference: Provided Room ID does not exist |
| **500** | Internal failure (Sanitized for security) |

---

## Demonstration Commands

*Note: Ensure the local server is active at `http://localhost:8080`.*

### 1. Register a Room
```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{
    "id": "ENG-205",
    "name": "Engineering Seminar Room",
    "capacity": 40
  }'
```

### 2. Enroll a Sensor
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{
    "id": "CO2-042",
    "type": "CO2",
    "status": "ACTIVE",
    "currentValue": 0.0,
    "roomId": "ENG-205"
  }'
```

### 3. Record a Measurement
```bash
curl -X POST http://localhost:8080/api/v1/sensors/CO2-042/readings \
  -H "Content-Type: application/json" \
  -d '{ "value": 412.7 }'
```

---

## Architecture Report & Analysis

---

### Part 1 — Service Architecture & Setup

#### Q1: Default lifecycle of a JAX-RS Resource class — per-request or singleton?

In JAX-RS (specifically Jersey), the default behavior is a **per-request lifecycle**. This means the framework creates a new instance of the resource class for every incoming HTTP call. Because these instances don't persist, storing long-term data in instance variables isn't possible, as they would be lost after the request finishes. To solve this, I built a **Thread-Safe Singleton DataStore**. This pattern uses `ConcurrentHashMap` objects to keep data stable across the program, regardless of how many resource objects are created. I chose `ConcurrentHashMap` over a standard `HashMap` to prevent potential multi-threading conflicts.

#### Q2: Why is HATEOAS considered a hallmark of advanced RESTful design?

**HATEOAS (Hypermedia As The Engine Of Application State)** is an architectural style where API responses include navigation links. This means clients don't need a hardcoded list of URLs; instead, the server informs them of available actions. This is superior to static documentation because if the server's URL structure changes, the client won't break—it simply follows the dynamic links provided. My `/api/v1` "Discovery" endpoint is a practical example of this approach toward making the API self-describing.

---

### Part 2 — Room Management

#### Q3: Returning IDs vs full objects in a list response

Returning only a list of IDs (e.g., `["R1", "R2"]`) results in a smaller initial response, but it forces the client to make numerous follow-up requests to get actual room data. This is inefficient and increases network latency. Alternatively, **returning full objects** in the first list allows the client to get all necessary information in one go, solving the "N+1" request problem. For the Smart Campus use case, where managers need a complete view of rooms quickly, this is the better design choice.

#### Q4: Is DELETE idempotent in this implementation?

**Yes, it is.** In REST, an operation is idempotent if repeating the request doesn't change the server's state further. In this app, the first `DELETE` removes the room. Any subsequent calls will see it's already gone and won't modify the data again. Even though the response codes differ (`204 No Content` for the first call and `404 Not Found` for later ones), the data state remains the same. Idempotency is defined by the absence of side effects on data, not by identical response codes.

---

### Part 3 — Sensor Operations & Filtering

#### Q5: Technical consequences of mismatched @Consumes content type

By using the `@Consumes(MediaType.APPLICATION_JSON)` annotation, a strict requirement for incoming data is enforced. If a client sends a different header (like `text/plain`), the request is blocked, and the server automatically returns a **415 Unsupported Media Type** error. This built-in validation ensures that incompatible data never reaches the backend logic, keeping the implementation safe and predictable.

#### Q6: Query parameter (?type=CO2) vs path segment (/sensors/type/CO2) for filtering

**Query parameters** (`?type=CO2`) are the standard way to filter collections. While path segments usually point to a specific, unique resource, query parameters are intended for modifying the view of a list. They are easier to implement and support optional criteria—if no type is provided, the API just returns everything. They also make it simple to combine multiple filters (like `?type=CO2&status=ACTIVE`) without creating complex or "messy" URL structures.

---

### Part 4 — Deep Nesting with Sub-Resources

#### Q7: Architectural benefits of the Sub-Resource Locator pattern

The **Sub-Resource Locator** pattern lets the `SensorResource` delegate specific paths to a dedicated `SensorReadingResource` class. Instead of stuffing all functionality into one giant file, this approach distributes responsibilities across smaller classes. This follows the principle of **Separation of Concerns**, ensuring the codebase remains modular and sustainable as the API grows. It also allows for efficient contextual injection from parent to child.

---

### Part 5 — Error Handling, Exception Mapping & Logging

#### Q8: Why HTTP 422 is more accurate than 404 for a missing roomId reference

A `404 Not Found` error should be used when the URL path itself is missing. However, a **422 Unprocessable Entity** is more appropriate when the request is syntactically correct but the data inside it is logically invalid. For instance, if a user tries to link a sensor to a non-existent room ID, the endpoint exists (no 404), but the content is wrong. 422 tells the client that the JSON was understood but the specific data provided cannot be processed.

#### Q9: Cybersecurity risks of exposing Java stack traces

Leaking raw Java stack traces is a major security risk because it reveals internal package structures, library versions (which may have known exploits), and server filesystem information. I prevent this information disclosure by using a **GlobalExceptionMapper**. This catches unhandled errors and returns a sanitized **500 Internal Server Error** message instead of raw technical details that could be exploited.

#### Q10: Why use JAX-RS filters for logging instead of manual Logger.info() calls

Logging is a **cross-cutting concern** that should be handled globally. Manually adding `Logger.info()` calls to every endpoint is repetitive and violates the DRY (Don't Repeat Yourself) principle. By using **JAX-RS filters** (`ContainerRequestFilter` and `ContainerResponseFilter`), all inbound and outbound traffic can be logged in a single place. This ensures consistency and keeps resource classes clean and focused solely on business logic.

---

*Documentation prepared for the 5COSC022W submission — University of Westminster, Academic Year 2025/26.*
