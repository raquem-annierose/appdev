# Activity GET and POST — How It Works

This document covers how the app handles adding an activity (POST) and retrieving all activities (GET), using a layered architecture: Controller → Service → Repository → Database (or In-Memory Storage).

---

## Architecture Overview

```
Client (Postman/Browser)
        ↓
ActivityController          ← receives HTTP requests
        ↓
ActivityService (interface) ← defines what operations are available
        ↓
ActivityServiceImpl         ← actual logic lives here
        ↓
In-Memory List / Repository ← talks to the database via JPA
        ↓
MySQL Database (optional) or In-Memory Storage
```

---

## The Layers

### 1. Model — `Activity.java`

Represents an activity entity with the following fields:

| Field | Type | Constraint |
|---|---|---|
| `id` | `Long` | Unique identifier (can be auto-generated) |
| `name` | `String` | Activity name, required |
| `description` | `String` | Activity description, nullable |
| `createdAt` | `LocalDateTime` | Timestamp when activity was created |

Current implementation uses in-memory storage, but the model is ready for database integration with JPA annotations.

---

### 2. DTOs (Data Transfer Objects)

DTOs separate what the client sends/receives from the internal model.

**`ActivityRequestDTO`** — what the client sends in the POST body:
```json
{
  "name": "Spring Boot Workshop",
  "description": "Learn Spring Boot fundamentals"
}
```

**`ActivityResponseDTO`** — what the client gets back in GET responses:
```json
{
  "id": 1,
  "name": "Spring Boot Workshop",
  "description": "Learn Spring Boot fundamentals",
  "createdAt": "2026-04-25T10:30:00"
}
```

---

### 3. Repository — `ActivityRepository` *Not Yet Implemented*

The current implementation uses an in-memory `ArrayList<Activity>` in `ActivityServiceImpl`. For database integration, extend `JpaRepository<Activity, Long>` to get `save()`, `findAll()`, `findById()`, etc. for free.

---

### 4. Service Interface — `ActivityService.java`

Defines the contract (what methods must exist):

```java
Long createActivity(ActivityRequestDTO activity);
List<ActivityResponseDTO> getAllActivities();
Activity getActivityById(Long id);
Activity updateActivity(Long id, String name, String description);
boolean deleteActivity(Long id);
```

---

### 5. Service Implementation — `ActivityServiceImpl.java`

Implements the actual logic using in-memory storage.

**`createActivity()`** — POST flow:
1. Receives an `ActivityRequestDTO` from the controller
2. Creates a new `Activity` object from the DTO fields
3. Adds it to the in-memory `activities` list
4. Returns the generated `id`

**`getAllActivities()`** — GET flow:
1. Fetches all `Activity` objects from in-memory storage
2. Loops through each `Activity` and maps it to an `ActivityResponseDTO`
3. Returns the list of DTOs

**`getActivityById()`** — GET by ID flow:
1. Loops through the in-memory list to find matching ID
2. Returns the `Activity` if found, `null` if not found

---

### 6. Controller — `ActivityController.java`

Handles HTTP requests at base path `/activities`.

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/activities` | Add a new activity, returns the generated activity ID |
| `GET` | `/activities` | Get all activities as a list |
| `GET` | `/activities/{id}` | Get an activity by ID |
| `PUT` | `/activities/{id}` | Update an activity by ID |
| `DELETE` | `/activities/{id}` | Delete an activity by ID |

**POST `/activities`**
```java
@PostMapping
public Long createActivity(@RequestBody ActivityRequestDTO activity) {
    Long result = null;
    if (Objects.nonNull(activity)) {
        result = activityService.createActivity(activity);
    }
    return result;
}
```
- Reads the JSON body into `ActivityRequestDTO`
- Passes it to `activityService.createActivity()`
- Returns the new activity's generated `id`

**Example request:**
```json
{
  "name": "Spring Boot Workshop",
  "description": "Learn Spring Boot fundamentals"
}
```

**Example response:**
```json
1
```

**GET `/activities`**
```java
@GetMapping
public List<ActivityResponseDTO> getAllActivities() {
    return activityService.getAllActivities();
}
```
- Calls `activityService.getAllActivities()`
- Returns JSON array of all activities as DTOs

**Example response:**
```json
[
  {
    "id": 1,
    "name": "Spring Boot Workshop",
    "description": "Learn Spring Boot fundamentals",
    "createdAt": "2026-04-25T10:30:00.123456"
  },
  {
    "id": 2,
    "name": "Java OOP Training",
    "description": "Object-oriented programming concepts",
    "createdAt": "2026-04-25T11:15:00.654321"
  }
]
```

**GET `/activities/{id}`**
```java
@GetMapping("/{id}")
public Activity getActivityById(@PathVariable Long id) {
    return activityService.getActivityById(id);
}
```
- Retrieves an activity by its ID
- Returns the `Activity` object if found, or `null` if not found

---

## Current Implementation Notes

- **Storage**: In-memory `ArrayList` (lost on application restart)
- **ID Generation**: Currently not auto-generated — needs implementation
- **Database**: Not yet integrated; ready for JPA/Hibernate migration

---

## Flow Summary

### POST (Add Activity)
```
POST /activities
Body: { "name": "Spring Boot Workshop", "description": "Learn Spring Boot fundamentals" }

→ Controller receives ActivityRequestDTO
→ Calls activityService.createActivity(request)
→ Service creates Activity object, adds to in-memory list
→ In-memory list generates the id
→ Controller returns: 1  (the id)
```

### GET (Get All Activities)
```
GET /activities

→ Controller calls activityService.getAllActivities()
→ Service fetches all Activity objects from in-memory list
→ Service maps each Activity → ActivityResponseDTO
→ Controller returns JSON array of activities as DTOs
```

### GET (Get Activity by ID)
```
GET /activities/{id}

→ Controller receives id as @PathVariable
→ Calls activityService.getActivityById(id)
→ Service loops through activities and finds matching ID
→ Returns the Activity if found, or null if not found
```

---

## Future Enhancements

1. **Database Integration**: Replace in-memory storage with JPA/Hibernate
2. **Auto-ID Generation**: Use `@GeneratedValue` annotation
3. **DTOs**: Create `ActivityRequestDTO` and `ActivityResponseDTO`
4. **Request Body**: Use `@RequestBody` instead of `@RequestParam` for cleaner API
5. **Validation**: Add Spring validation annotations (`@NotNull`, `@NotBlank`, etc.)
6. **Error Handling**: Implement proper exception handling for not-found scenarios
