# Student CRUD Implementation with MyBatis

Complete CRUD (Create, Read, Update, Delete) implementation for Student entity using MyBatis.

## Architecture Overview

```
Controller Layer (StudentController / UserRestController)
    ↓
Service Layer (StudentService / StudentServiceImpl)
    ↓
Repository Layer (StudentMapper interface)
    ↓
MyBatis XML Mapper (StudentMapper.xml)
    ↓
Database (student table)
```

## Files Modified/Created

### 1. **StudentMapper.java** (Repository Interface)
**Location:** `src/main/java/com/pup/taguig/app/repositoryM/StudentMapper.java`

```java
@Mapper
public interface StudentMapper {
    public StudentM getUserById(Long id);
    public List<StudentM> retrieveAllStudent();
    public Long insertStudent(StudentM student);
    public int updateStudent(StudentM student);
    public int deleteStudent(Long id);
    public List<StudentM> searchByName(@Param("lastName") String lastName, 
                                       @Param("firstName") String firstName);
}
```

### 2. **StudentMapper.xml** (MyBatis SQL Mapper)
**Location:** `src/main/resources/mappers/StudentMapper.xml`

**Operations:**
- `getUserById` - SELECT student by ID
- `retrieveAllStudent` - SELECT all students
- `searchByName` - SELECT students by first and last name
- `insertStudent` - INSERT new student (auto-generates ID)
- `updateStudent` - UPDATE student by ID
- `deleteStudent` - DELETE student by ID

### 3. **StudentService.java** (Service Interface)
**Location:** `src/main/java/com/pup/taguig/app/service/StudentService.java`

```java
public interface StudentService {
    public StudentResponseDTO getUserById(Long id);
    public List<StudentResponseDTO> retrieveAllStudent();
    public Long insertStudent(StudentRequestDTO student);
    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO student);
    public boolean deleteStudent(Long id);
    public List<StudentResponseDTO> searchByName(String lastName, String firstName);
}
```

### 4. **StudentServiceImpl.java** (Service Implementation)
**Location:** `src/main/java/com/pup/taguig/app/service/impl/StudentServiceImpl.java`

**Features:**
- Input validation (first name and last name required)
- Null checks for safety
- Converts between StudentM (MyBatis model) and DTOs
- Proper error handling with IllegalArgumentException

### 5. **StudentController.java** (REST Controller)
**Location:** `src/main/java/com/pup/taguig/app/controller/StudentController.java`

**Base URL:** `/api/students`

### 6. **UserRestController.java** (Alternative REST Controller)
**Location:** `src/main/java/com/pup/taguig/app/controller/UserRestController.java`

**Base URL:** `/user`

## API Endpoints

### StudentController (`/api/students`)

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/api/students` | Get all students | - | List<StudentResponseDTO> |
| GET | `/api/students/{id}` | Get student by ID | - | StudentResponseDTO |
| GET | `/api/students/search?firstName=X&lastName=Y` | Search by name | - | List<StudentResponseDTO> |
| POST | `/api/students` | Create new student | StudentRequestDTO | Long (ID) |
| PUT | `/api/students/{id}` | Update student | StudentRequestDTO | StudentResponseDTO |
| DELETE | `/api/students/{id}` | Delete student | - | 204 No Content |

### UserRestController (`/user`)

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/user/` | Get all students | - | List<StudentResponseDTO> |
| GET | `/user/{id}` | Get student by ID | - | StudentResponseDTO |
| GET | `/user/filter?firstName=X&lastName=Y` | Search by name | - | List<StudentResponseDTO> |
| POST | `/user/` | Create new student | StudentRequestDTO | Long (ID) |
| PUT | `/user/{id}` | Update student | StudentRequestDTO | StudentResponseDTO |
| DELETE | `/user/{id}` | Delete student | - | boolean |

## Request/Response DTOs

### StudentRequestDTO
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "midtermGrade": 85.5,
  "finalGrade": 90.0
}
```

### StudentResponseDTO
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "midtermGrade": 85.5,
  "finalGrade": 90.0
}
```

## Testing the API

### 1. Create a Student
```bash
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "midtermGrade": 85.5,
    "finalGrade": 90.0
  }'
```

### 2. Get All Students
```bash
curl http://localhost:8080/api/students
```

### 3. Get Student by ID
```bash
curl http://localhost:8080/api/students/1
```

### 4. Search Students by Name
```bash
curl "http://localhost:8080/api/students/search?firstName=John&lastName=Doe"
```

### 5. Update a Student
```bash
curl -X PUT http://localhost:8080/api/students/1 \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jane",
    "lastName": "Doe",
    "midtermGrade": 88.0,
    "finalGrade": 92.0
  }'
```

### 6. Delete a Student
```bash
curl -X DELETE http://localhost:8080/api/students/1
```

## Database Schema

```sql
CREATE TABLE student (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    midterm_grade FLOAT,
    final_grade FLOAT
);
```

## Key Features

1. **Complete CRUD Operations** - All basic database operations implemented
2. **Input Validation** - Required fields validated in service layer
3. **Null Safety** - Proper null checks throughout the code
4. **DTO Pattern** - Separation between request/response DTOs and entity models
5. **MyBatis Integration** - XML-based SQL mapping with auto-generated keys
6. **RESTful Design** - Proper HTTP methods and status codes
7. **Search Functionality** - Filter students by first and last name
8. **Two Controller Options** - Both `/api/students` and `/user` endpoints available

## Error Handling

- **400 Bad Request** - Invalid input or missing required fields
- **404 Not Found** - Student with specified ID doesn't exist
- **201 Created** - Student successfully created
- **204 No Content** - Student successfully deleted

## Notes

- The `StudentM` model is used for MyBatis operations (no JPA annotations)
- The `Student` model is the JPA entity (not used in this MyBatis implementation)
- Auto-increment ID is handled by the database and MyBatis `useGeneratedKeys`
- All string inputs are trimmed to remove leading/trailing whitespace
