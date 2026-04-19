# Student GET and POST — How It Works

This document covers how the app handles adding a student (POST) and retrieving all students (GET), using a layered architecture: Controller → Service → Repository → Database.

---

## Architecture Overview

```
Client (Postman/Browser)
        ↓
UserRestController       ← receives HTTP requests
        ↓
UserService (interface)  ← defines what operations are available
        ↓
UserServiceImpl          ← actual logic lives here
        ↓
StudentRepository        ← talks to the database via JPA
        ↓
MySQL Database (appdev_db_try)
```

---

## The Layers

### 1. Model — `Student.java`

Represents a row in the `student` table in MySQL. Uses JPA annotations to define the schema.

| Field | Type | Constraint |
|---|---|---|
| `id` | `long` | Auto-incremented primary key (`@GeneratedValue`) |
| `firstName` | `String` | Not null, max 100 chars, unique |
| `lastName` | `String` | Not null, cannot be updated after insert |
| `midtermGrade` | `float` | Nullable |
| `finalGrade` | `float` | Nullable |

Extra methods:
- `compute()` — averages midterm and final grade
- `evaluate()` — returns `"Pass"` if average >= 75, else `"Failed"`

---

### 2. DTOs (Data Transfer Objects)

DTOs separate what the client sends/receives from the internal model.

**`StudentRequestDTO`** — what the client sends in the POST body:
```json
{
  "firstName": "Annie",
  "lastName": "Raquem",
  "midtermGrade": 85.0,
  "finalGrade": 90.0
}
```

**`StudentResponseDTO`** — what the client gets back in GET responses:
```json
{
  "id": 1,
  "firstName": "Annie",
  "lastName": "Raquem",
  "midtermGrade": 85.0,
  "finalGrade": 90.0
}
```

---

### 3. Repository — `StudentRepository`

Extends `JpaRepository<Student, Long>` — gives us `save()`, `findAll()`, `findById()`, etc. for free without writing SQL.

---

### 4. Service Interface — `UserService.java`

Defines the contract (what methods must exist):

```java
Long addUser(StudentRequestDTO student);
List<StudentResponseDTO> retrieveAllStudent();
Student getUserById(Long id);
List<StudentResponseDTO> searchByName(String name, String firstName);
boolean deleteStudent(Long id);
```

---

### 5. Service Implementation — `UserServiceImpl.java`

Implements the actual logic.

**`addUser()`** — POST flow:
1. Receives a `StudentRequestDTO` from the controller
2. Creates a new `Student` object from the DTO fields
3. Calls `studentRepository.save(student)` — saves to MySQL, auto-generates the `id`
4. Returns the generated `id`

**`retrieveAllStudent()`** — GET flow:
1. Calls `studentRepository.findAll()` — fetches all rows from MySQL
2. Loops through each `Student` and maps it to a `StudentResponseDTO`
3. Returns the list of DTOs

---

### 6. Controller — `UserRestController.java`

Handles HTTP requests at base path `/user`.

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/user/` | Add a new student, returns the generated `id` |
| `GET` | `/user/` | Get all students as a list |
| `GET` | `/user/{id}` | Get a student by ID (from in-memory list, not DB yet) |
| `GET` | `/user/filter` | Search by `lastName` and `firstName` query params |
| `DELETE` | `/user/{id}` | Delete a student by ID |

**POST `/user/`**
```java
@PostMapping("/")
public Long addStudent(@RequestBody StudentRequestDTO student) {
    Long result = null;
    if (Objects.nonNull(student)) {
        result = userService.addUser(student);
    }
    return result;
}
```
- Reads the JSON body into `StudentRequestDTO`
- Passes it to `userService.addUser()`
- Returns the new student's auto-generated `id`

**GET `/user/`**
```java
@GetMapping("/")
public List<StudentResponseDTO> getAllUsers() {
    return userService.retrieveAllStudent();
}
```
- Calls `userService.retrieveAllStudent()`
- Returns JSON array of all students from the database

---

## Database Configuration (`application.properties`)

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/appdev_db_try
spring.datasource.username=niera
spring.datasource.password=appdevpass123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update   # auto-creates/updates table schema
spring.jpa.show-sql=true               # prints SQL queries in console
spring.jpa.properties.hibernate.format_sql=true
```

`ddl-auto=update` means Hibernate automatically creates the `student` table if it doesn't exist, or updates columns if the model changes.

---

## Flow Summary

### POST (Add Student)
```
POST /user/
Body: { "firstName": "Annie", "lastName": "Raquem", "midtermGrade": 85, "finalGrade": 90 }

→ Controller receives StudentRequestDTO
→ Calls userService.addUser(request)
→ Service creates Student object, calls studentRepository.save()
→ MySQL inserts row, returns auto-generated id
→ Controller returns: 1  (the id)
```

### GET (Get All Students)
```
GET /user/

→ Controller calls userService.retrieveAllStudent()
→ Service calls studentRepository.findAll()
→ MySQL returns all rows
→ Service maps each Student → StudentResponseDTO
→ Controller returns JSON array of students
```
