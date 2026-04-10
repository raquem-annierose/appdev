# ActivityController Guide - REST API Explained

## File Location
`src/main/java/com/pup/taguig/app/controller/ActivityController.java`

## Overview

Ang **ActivityController** ay isang REST API controller na nag-manage ng activities (tasks). Gumagamit ito ng **in-memory storage** (ArrayList) para sa data at nag-implement ng complete **CRUD** (Create, Read, Update, Delete) operations.

---

## Key Components

### 1. **Controller Annotation**
```java
@RestController
@RequestMapping("/activities")
public class ActivityController { ... }
```

- **@RestController:** Ang class na ito ay REST endpoint handler
- **@RequestMapping("/activities"):** Base URL path ay `/activities`

**Accessible URLs:**
- `GET /activities` - List all
- `GET /activities/{id}` - Get one
- `POST /activities` - Create
- `PUT /activities/{id}` - Update
- `DELETE /activities/{id}` - Delete

---

### 2. **Data Storage**
```java
private final List<Activity> activities = new ArrayList<>();
private final AtomicLong idCounter = new AtomicLong(1);
```

- **activities:** In-memory list (hindi persistent - mawawala sa restart)
- **idCounter:** Auto-incrementing ID generator using AtomicLong (thread-safe)

📝 **Note:** Real systems gumagamit ng database (MySQL, PostgreSQL)

---

### 3. **Data Models**

#### **Activity Class** (Main entity)
```java
static class Activity {
    private Long id;                      // Unique identifier
    private String name;                  // Activity name
    private String description;           // Activity details
    private LocalDateTime createdAt;      // Timestamp
    
    // Constructor
    public Activity(Long id, String name, String description, LocalDateTime createdAt) { ... }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    // ... more getters/setters
}
```

**OOP Concept:** Encapsulation - private attributes with public accessors

---

#### **ActivityRequest Class** (Data Transfer Object)
```java
static class ActivityRequest {
    private String name;
    private String description;
    
    // Getters and Setters
}
```

**Why DTO?**
- Client ay hindi kailangan mag-send ng `id` at `createdAt` (auto-generated)
- Protektado ang model from direct manipulation

---

## REST Operations (CRUD)

### **1️⃣ CREATE - POST /activities**

```java
@PostMapping
public ResponseEntity<?> createActivity(@RequestBody ActivityRequest request) {
    // Validation
    if (request.getName() == null || request.getName().trim().isEmpty()) {
        return ResponseEntity.badRequest().body("Name is required.");
    }
    
    // Create new Activity
    Activity activity = new Activity(
            idCounter.getAndIncrement(),    // Auto-increment ID
            request.getName().trim(),
            request.getDescription(),
            LocalDateTime.now()             // Set current timestamp
    );
    
    // Add to list
    activities.add(activity);
    
    // Return 201 CREATED + object
    return ResponseEntity.status(HttpStatus.CREATED).body(activity);
}
```

**How it works:**

1. **Receive request** (JSON from client)
   ```json
   {
     "name": "Complete homework",
     "description": "Finish math assignment"
   }
   ```

2. **Validate** - Check kung may name

3. **Create Activity object** 
   - Auto-generate ID (1, 2, 3...)
   - Set current timestamp (LocalDateTime.now())

4. **Store** - Add sa ArrayList

5. **Return** 
   - Status: `201 Created`
   - Body: Complete Activity object with ID at timestamp

**Example Response:**
```json
201 Created
{
  "id": 1,
  "name": "Complete homework",
  "description": "Finish math assignment",
  "createdAt": "2024-04-11T10:30:45.123456"
}
```

---

### **2️⃣ READ - GET Operations**

#### **GET All Activities**
```java
@GetMapping
public ResponseEntity<List<Activity>> getAllActivities() {
    return ResponseEntity.ok(activities);
}
```

- **Endpoint:** `GET /activities`
- **Returns:** HTTP 200 OK + List of all activities

**Example Response:**
```json
200 OK
[
  {
    "id": 1,
    "name": "Homework",
    "description": "Math",
    "createdAt": "2024-04-11T10:30:45"
  },
  {
    "id": 2,
    "name": "Study",
    "description": "Java",
    "createdAt": "2024-04-11T11:00:00"
  }
]
```

---

#### **GET Single Activity by ID**
```java
@GetMapping("/{id}")
public ResponseEntity<?> getActivityById(@PathVariable Long id) {
    // Loop through list to find activity
    for (Activity activity : activities) {
        if (activity.getId().equals(id)) {
            return ResponseEntity.ok(activity);
        }
    }
    
    // If not found
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("Activity with ID " + id + " not found.");
}
```

- **Endpoint:** `GET /activities/{id}`
- **Parameter:** `{id}` - taken from URL path (@PathVariable)

**Examples:**

✅ **Success:**
```
GET /activities/1

200 OK
{
  "id": 1,
  "name": "Homework",
  "description": "Math",
  "createdAt": "2024-04-11T10:30:45"
}
```

❌ **Not Found:**
```
GET /activities/999

404 Not Found
"Activity with ID 999 not found."
```

---

### **3️⃣ UPDATE - PUT /activities/{id}**

```java
@PutMapping("/{id}")
public ResponseEntity<?> updateActivity(@PathVariable Long id, @RequestBody ActivityRequest request) {
    // Validation
    if (request.getName() == null || request.getName().trim().isEmpty()) {
        return ResponseEntity.badRequest().body("Name is required.");
    }
    
    // Find and update
    for (Activity activity : activities) {
        if (activity.getId().equals(id)) {
            activity.setName(request.getName().trim());
            activity.setDescription(request.getDescription());
            return ResponseEntity.ok(activity);
        }
    }
    
    // Not found
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("Activity with ID " + id + " not found.");
}
```

- **Endpoint:** `PUT /activities/{id}`
- **Purpose:** Replace entire Activity with new data

**Process:**

1. **Find** activity by ID
2. **Update** name at description
3. **Keep** old ID at createdAt (unchanged)
4. **Return** updated Activity

**Example Request:**
```
PUT /activities/1

{
  "name": "Complete homework UPDATED",
  "description": "Math and Science"
}
```

**Response:**
```json
200 OK
{
  "id": 1,
  "name": "Complete homework UPDATED",
  "description": "Math and Science",
  "createdAt": "2024-04-11T10:30:45"  // NOT changed
}
```

---

### **4️⃣ DELETE - DELETE /activities/{id}**

```java
@DeleteMapping("/{id}")
public ResponseEntity<?> deleteActivity(@PathVariable Long id) {
    // Remove from list
    boolean removed = activities.removeIf(activity -> activity.getId().equals(id));
    
    if (!removed) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Activity with ID " + id + " not found.");
    }
    
    return ResponseEntity.ok("Activity with ID " + id + " deleted successfully.");
}
```

- **Endpoint:** `DELETE /activities/{id}`
- **Purpose:** Remove activity permanently

**How removeIf() works:**
```
removeIf(condition) - removes element if condition ay true
```

**Example:**

✅ **Success:**
```
DELETE /activities/1

200 OK
"Activity with ID 1 deleted successfully."
```

❌ **Not Found:**
```
DELETE /activities/999

404 Not Found
"Activity with ID 999 not found."
```

---

## HTTP Methods Comparison

| HTTP Method | Operation | Endpoint | Purpose |
|---|---|---|---|
| **POST** | CREATE | `/activities` | Add new activity |
| **GET** | READ (all) | `/activities` | Get all activities |
| **GET** | READ (one) | `/activities/{id}` | Get specific activity |
| **PUT** | UPDATE | `/activities/{id}` | Modify existing activity |
| **DELETE** | DELETE | `/activities/{id}` | Remove activity |

---

## Response Entities

Ang lahat ng methods ay nag-return ng **ResponseEntity**:

```java
ResponseEntity<String>       → Returns string + HTTP status
ResponseEntity<Activity>     → Returns Activity object + HTTP status
ResponseEntity<List<Activity>> → Returns list + HTTP status
ResponseEntity<?>           → Returns any type (generic)
```

**Common HTTP Status Codes:**
- **200 OK** - Success
- **201 Created** - Resource created successfully
- **400 Bad Request** - Invalid input
- **404 Not Found** - Resource doesn't exist

---

## Testing with cURL or Postman

### **Create Activity**
```bash
curl -X POST http://localhost:8080/activities \
  -H "Content-Type: application/json" \
  -d '{"name":"Buy groceries","description":"Milk, bread, eggs"}'
```

### **Get All Activities**
```bash
curl http://localhost:8080/activities
```

### **Get Specific Activity**
```bash
curl http://localhost:8080/activities/1
```

### **Update Activity**
```bash
curl -X PUT http://localhost:8080/activities/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Buy groceries UPDATED","description":"New list"}'
```

### **Delete Activity**
```bash
curl -X DELETE http://localhost:8080/activities/1
```

---

## OOP Concepts in This Controller

### **1. Encapsulation**
- Activity fields ay private
- Access through getters/setters

### **2. Abstraction**
- Controller ay hindi nag-expose ng internal storage details
- Client ay nakikita lang ang Activity model

### **3. Polymorphism**
- ResponseEntity<T> generic - works with any type
- Different status codes with same method

### **4. Single Responsibility**
- Controller = HTTP handling only
- Business logic → moved to Service layer (best practice)

---

## Important Notes

⚠️ **Limitations of Current Implementation:**
- ✗ Data lost on restart (in-memory only)
- ✗ Not thread-safe for concurrent requests (ArrayList)
- ✗ No database persistence
- ✗ No authentication/authorization

✅ **Production Improvements:**
- Add JPA/Hibernate for database
- Use Spring Data Repository
- Add @Service layer for business logic
- Add Spring Security
- Add validation annotations (@Valid, @NotNull)

---

## File Location Reference
- **Controller:** `src/main/java/com/pup/taguig/app/controller/ActivityController.java`
- **Application Entry Point:** `src/main/java/com/pup/taguig/app/SpringBootWeb1Application.java`
- **Application Properties:** `src/main/resources/application.properties`
