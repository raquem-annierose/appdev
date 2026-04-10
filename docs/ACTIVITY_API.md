# Activity REST API

A Spring Boot REST API that performs full CRUD operations for an **Activity** resource. Data is stored in-memory using a `List`.

---

## Project Structure

```
src/main/java/com/pup/taguig/app/
│
├── model/
│   └── Activity.java              # Entity class
│
├── dto/
│   └── ActivityRequest.java       # Request body (input)
│
├── service/
│   ├── ActivityService.java       # Interface (contract)
│   └── impl/
│       └── ActivityServiceImpl.java  # Implementation (logic + storage)
│
└── controller/
    └── ActivityController.java    # HTTP endpoints
```

---

## Layers Explained

### 1. Model — `Activity.java`

Represents the **Activity entity**. This is the data that gets stored and returned by the API.

| Field | Type | Description |
|-------|------|-------------|
| `id` | `Long` | Auto-generated unique identifier |
| `name` | `String` | Name of the activity (required) |
| `description` | `String` | Details about the activity (optional) |
| `createdAt` | `LocalDateTime` | Timestamp set automatically on creation |

```java
public class Activity {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    // constructor, getters, setters
}
```

---

### 2. DTO — `ActivityRequest.java`

A **Data Transfer Object** used to receive input from the client (e.g., Postman request body). It only contains the fields the user is allowed to send — `id` and `createdAt` are excluded because those are set by the server.

```java
public class ActivityRequest {
    private String name;
    private String description;
    // getters, setters
}
```

**Why separate from the model?**
The model is the internal entity. The DTO is what the client sends. Keeping them separate prevents clients from overwriting server-controlled fields like `id`.

---

### 3. Service Interface — `ActivityService.java`

Defines the **contract** (what operations are available) without specifying how they work. This is the OOP principle of **programming to an interface**.

```java
public interface ActivityService {
    Activity createActivity(String name, String description);
    List<Activity> getAllActivities();
    Activity getActivityById(Long id);
    Activity updateActivity(Long id, String name, String description);
    boolean deleteActivity(Long id);
}
```

---

### 4. Service Implementation — `ActivityServiceImpl.java`

Contains the **actual business logic**. Annotated with `@Service` so Spring automatically detects and registers it as a bean.

- Holds the in-memory `ArrayList<Activity>` — this acts as the "database"
- Uses `AtomicLong` to safely auto-generate unique IDs
- Sets `createdAt` automatically using `LocalDateTime.now()` on creation

```java
@Service
public class ActivityServiceImpl implements ActivityService {
    private final List<Activity> activities = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);
    // implements all 5 CRUD methods
}
```

---

### 5. Controller — `ActivityController.java`

Handles **HTTP requests** and maps them to service calls. Annotated with `@RestController` and `@RequestMapping("/activities")`.

Uses `@Autowired` to inject `ActivityService` — the controller does not know or care that `ActivityServiceImpl` exists; it only talks to the interface.

```java
@RestController
@RequestMapping("/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;
    // endpoint methods
}
```

---

## API Endpoints

Base URL: `http://localhost:8080/activities`

### POST `/activities` — Create an Activity

**Request Body:**
```json
{
  "name": "Morning Run",
  "description": "5km jog around the park"
}
```

**Response `201 Created`:**
```json
{
  "id": 1,
  "name": "Morning Run",
  "description": "5km jog around the park",
  "createdAt": "2026-03-29T08:00:00"
}
```

> `name` is required. Returns `400 Bad Request` if missing.

---

### GET `/activities` — Get All Activities

**Response `200 OK`:**
```json
[
  {
    "id": 1,
    "name": "Morning Run",
    "description": "5km jog around the park",
    "createdAt": "2026-03-29T08:00:00"
  }
]
```

---

### GET `/activities/{id}` — Get Activity by ID

**Response `200 OK`:**
```json
{
  "id": 1,
  "name": "Morning Run",
  "description": "5km jog around the park",
  "createdAt": "2026-03-29T08:00:00"
}
```

> Returns `404 Not Found` if ID does not exist.

---

### PUT `/activities/{id}` — Update an Activity

**Request Body:**
```json
{
  "name": "Evening Run",
  "description": "Updated to evening schedule"
}
```

**Response `200 OK`:**
```json
{
  "id": 1,
  "name": "Evening Run",
  "description": "Updated to evening schedule",
  "createdAt": "2026-03-29T08:00:00"
}
```

> `createdAt` is preserved and not changed on update. Returns `404` if ID not found.

---

### DELETE `/activities/{id}` — Delete an Activity

**Response `200 OK`:**
```
Activity with ID 1 deleted successfully.
```

> Returns `404 Not Found` if ID does not exist.

---

## OOP Principles Applied

| Principle | Where |
|-----------|-------|
| **Encapsulation** | Fields in `Activity` and `ActivityRequest` are private with getters/setters |
| **Abstraction** | `ActivityService` interface hides implementation details from the controller |
| **Separation of Concerns** | Each layer has one job: model = data, dto = input, service = logic, controller = HTTP |
| **Dependency Injection** | Controller depends on the interface, not the concrete class (`@Autowired`) |
