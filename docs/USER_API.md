# User REST API Documentation

## Overview
The User REST API provides CRUD operations for managing Student records in the application. This API is built using Spring Boot and follows RESTful principles.

---

## Base URL
```
/
```

---

## Endpoints

### 1. Get Student by ID
**Endpoint:** `GET /{id}`

**Description:** Retrieves a student record by their ID.

**Path Parameters:**
- `id` (Long): The unique identifier of the student

**Request Example:**
```
GET /123
```

**Response:**
```json
{
  "id": 123,
  "firstName": "Juan",
  "lastName": "Dela Cruz"
}
```

**Status Codes:**
- `200 OK`: Student found
- `204 No Content`: Student not found (returns null)

---

### 2. Add Student
**Endpoint:** `POST /`

**Description:** Creates a new student record.

**Request Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "firstName": "Juan",
  "lastName": "Dela Cruz"
}
```

**Response:**
```json
{
  "id": 12345678901,
  "firstName": "Juan",
  "lastName": "Dela Cruz"
}
```

**Status Codes:**
- `200 OK`: Student successfully created
- `400 Bad Request`: Invalid student data

**Notes:**
- Student ID is automatically generated using a timestamp-based calculation
- ID calculation includes: day of year, year, month, day of month, day of week, hour, minute, second, and nanosecond

---

### 3. Get All Students
**Endpoint:** `GET /all`

**Description:** Retrieves all student records from the system.

**Request Example:**
```
GET /all
```

**Response:**
```json
[
  {
    "id": 123,
    "firstName": "Juan",
    "lastName": "Dela Cruz"
  },
  {
    "id": 124,
    "firstName": "Maria",
    "lastName": "Santos"
  }
]
```

**Status Codes:**
- `200 OK`: Returns list of all students (can be empty)

---

### 4. Search Students by Name
**Endpoint:** `GET /filter`

**Description:** Searches for students by first name and/or last name.

**Query Parameters:**
- `name` (String, required): The name to search for (matches first name or last name)
- `firstName` (String, required): Filters by first name

**Request Example:**
```
GET /filter?name=Juan&firstName=Dela
```

**Response:**
```json
[
  {
    "id": 123,
    "firstName": "Juan",
    "lastName": "Dela Cruz"
  }
]
```

**Status Codes:**
- `200 OK`: Returns matching students
- `400 Bad Request`: Missing required query parameters (returns empty list)

**Search Logic:**
- Searches match if:
  - `name` equals first name (case-insensitive) OR
  - `name` equals last name (case-insensitive) OR
  - `name` equals full name: firstName + " " + lastName (case-insensitive)

---

### 5. Delete Student
**Endpoint:** `DELETE /{id}`

**Description:** Deletes a student record by ID.

**Request Parameters:**
- `id` (Long): The unique identifier of the student to delete

**Request Example:**
```
DELETE /123?id=123
```

**Response:**
```
true  (if successful)
false (if student not found)
```

**Status Codes:**
- `200 OK`: Returns boolean (true if deleted, false if not found)

---

## Data Model

### Student
```java
{
  "id": Long,           // Auto-generated ID
  "firstName": String,  // Student's first name
  "lastName": String    // Student's last name
}
```

---

## Error Handling

| Scenario | Response |
|----------|----------|
| Invalid student data | `null` or `400 Bad Request` |
| Student not found | `null` or `204 No Content` |
| Missing required parameters | Empty list `[]` returned |

---

## Implementation Notes

### UserRestController
- Validates object nullability before processing requests
- Uses Spring annotations: `@GetMapping`, `@PostMapping`, `@DeleteMapping`
- Implements defensive programming with `Objects.nonNull()` checks

### UserServiceImpl
- Stores students in an in-memory list
- **Note:** Data is not persistent; resets on application restart
- Uses case-insensitive search for name matching
- Generates unique IDs based on timestamp components

---

## Example Flow

```
1. POST / → Add new student
   Returns: Student with auto-generated ID

2. GET /all → Retrieve all students
   Returns: List of all students

3. GET /filter?name=Juan&firstName=Dela → Search by name
   Returns: Matching students

4. GET /{id} → Get specific student
   Returns: Student details or null

5. DELETE /{id}?id={id} → Remove student
   Returns: true if deleted, false if not found
```

---

## Future Improvements

- [ ] Implement database persistence (replace in-memory list)
- [ ] Add custom exception handling
- [ ] Implement logging
- [ ] Add input validation with `@Valid` annotation
- [ ] Add pagination for `/all` endpoint
- [ ] Implement proper HTTP status codes (201 Created, 404 Not Found, etc.)
- [ ] Add API documentation with Swagger/OpenAPI
- [ ] Implement transaction management
