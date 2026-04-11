# AppDev Architecture Guide - OOP Lessons

## Project Structure Overview

```
appdev/
├── pom.xml                          (Maven configuration)
├── src/
│   ├── main/
│   │   ├── java/com/pup/taguig/app/
│   │   │   ├── SpringBootWeb1Application.java    (Main entry point)
│   │   │   ├── controller/                       (Controller layer)
│   │   │   │   ├── ActivityController.java
│   │   │   │   ├── StudentController.java
│   │   │   │   └── UserRestController.java
│   │   │   ├── model/                            (Domain models)
│   │   │   │   ├── Calculator.java
│   │   │   │   ├── Student.java
│   │   │   │   └── User.java
│   │   │   ├── service/                          (Business logic)
│   │   │   │   ├── UserService.java
│   │   │   │   └── impl/
│   │   │   │       └── UserServiceImpl.java
│   │   │   ├── repository/                       (Data access)
│   │   │   ├── dto/                              (Data Transfer Objects)
│   │   │   └── exception/                        (Custom exceptions)
│   │   └── resources/
│   │       └── application.properties            (Configuration)
│   └── test/
│       └── java/                                 (Unit tests)
└── target/                                       (Compiled output)
```

---

## Layered Architecture (3-Tier)

Ang application ay gumagamit ng **Layered Architecture** pattern:

### **1. Controller Layer** (Request Handler)
📌 **Responsibility:** Sumatanggap ng HTTP requests at magpadala ng responses

```
Request (GET, POST, PUT, DELETE)
    ↓
Controller (Activity/User/StudentController)
    ↓
Service Layer
```

**OOP Concept:** 
- **Abstraction**: Ang controller ay nag-abstract ng HTTP details
- **Encapsulation**: Ang business logic ay hinihide sa service layer

---

### **2. Service Layer** (Business Logic)
📌 **Responsibility:** Gawin ang actual business logic, processing, at validation ng data

**Ano ang Service Layer?**

Ang Service Layer ay ang "brains" ng application - ito ang tumutukoy kung paano dapat mag-behave ang application based sa business requirements. Hindi ito direktang nakikipag-ugnayan sa HTTP requests o database operations. Ang role niya ay:

- ✅ **Mag-process ng business logic** - Validation, calculations, transformations
- ✅ **Mag-orchestrate** - Coordinate between multiple repositories kung kailangan
- ✅ **Mag-enforce ng business rules** - Ensure data integrity and consistency
- ✅ **Reusable** - Maaaring gamitin ng multiple controllers (web, API, mobile)

---

**OOP Concept:**
- **Polymorphism**: UserService (interface) → UserServiceImpl (implementation)
- **Abstraction**: Ang database queries ay abstracto mula sa controller
- **Single Responsibility**: Service = Business Logic lang, walang HTTP na involvement

```
Service Interface (UserService)
    ↓
Service Implementation (UserServiceImpl)
    ↓
Repository Layer
    ↓
Database
```

---

**Service Layer Architecture:**

```java
// 1. INTERFACE - Ang contract/agreement
public interface UserService {
    public Student addUser(Student student);  // Add new student
    public List<Student> retrieveAllStudent(); // Get all students
}

// 2. IMPLEMENTATION - Ang actual logic
public class UserServiceImpl implements UserService {
    
    // Inject repository para makipag-communicate sa database
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public Student addUser(Student student) {
        // Business logic before saving
        // 1. Validate student data
        if (student.getName() == null || student.getName().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        
        // 2. Apply business rules
        student.setCreatedAt(LocalDateTime.now());
        student.setStatus("ACTIVE");
        
        // 3. Call repository para mag-save sa database
        return userRepository.save(student);
    }
    
    @Override
    public List<Student> retrieveAllStudent() {
        // Business logic for retrieval
        List<Student> students = userRepository.findAll();
        
        // Can do additional processing here if needed
        // e.g., filter, sort, enrich data
        return students;
    }
}
```

---

**Flow ng Service Layer:**

```
1. CONTROLLER receives request
   ↓
2. CONTROLLER calls Service method
   @PostMapping("/students")
   public ResponseEntity<?> addStudent(@RequestBody Student student) {
       Student saved = userService.addUser(student);  ← Call service
       return ResponseEntity.ok(saved);
   }
   ↓
3. SERVICE executes business logic
   - Validate input data
   - Apply business rules
   - Call repository if needed
   ↓
4. SERVICE returns result to Controller
   ↓
5. CONTROLLER sends response to client
```

---

**Why Separate Service Layer?**

| **Without Service Layer** | **With Service Layer** |
|---|---|
| Controller may malaki at complex | Controller lean at focused sa HTTP |
| Business logic scattered everywhere | Business logic centralized |
| Hard to test business logic | Easy to test with mocks |
| Hard to reuse business logic | Reusable across multiple controllers |
| Database queries sa controller | Controllers hindi alam tungkol sa DB |

---

**Example ng Business Logic sa Service:**

```java
// UserServiceImpl.java
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService; // May dependency din sa iba
    
    public Student addUser(Student student) {
        // VALIDATION - Check if data is valid
        if (student.getAge() < 18) {
            throw new BusinessException("Student must be 18 or older");
        }
        
        // CHECK DUPLICATES - Business rule
        if (userRepository.existsByEmail(student.getEmail())) {
            throw new BusinessException("Email already registered");
        }
        
        // SET DEFAULTS - Business rule
        student.setStatus("PENDING");
        student.setRegistrationDate(LocalDateTime.now());
        
        // SAVE - Call repository
        Student savedStudent = userRepository.save(student);
        
        // SEND EMAIL - Orchestrate other services
        emailService.sendWelcomeEmail(savedStudent.getEmail());
        
        // RETURN - Give result back to controller
        return savedStudent;
    }
    
    public List<Student> retrieveAllStudent() {
        // Get from repository
        List<Student> students = userRepository.findAll();
        
        // Filter only active students (business rule)
        return students.stream()
            .filter(s -> "ACTIVE".equals(s.getStatus()))
            .collect(Collectors.toList());
    }
}
```

---

**Key Responsibilities ng Service Layer:**

| **Responsibility** | **Example** |
|---|---|
| **Validation** | Check if email format is valid, age >= 18 |
| **Business Rules** | Prevent duplicate records, enforce status transitions |
| **Data Transformation** | Convert Student to StudentDTO |
| **Orchestration** | Call multiple repositories, coordinate operations |
| **Transaction Management** | Ensure multiple DB operations succeed together |
| **Error Handling** | Catch exceptions, throw meaningful business exceptions |
| **Logging** | Log important business events |

---

**Service Layer vs Repository Layer:**

```
SERVICE LAYER (UserServiceImpl)
- "Should I save this student?" ← Business decision
- "Is this student eligible?" ← Business logic
- "What should happen after saving?" ← Orchestration
- "Format the response correctly" ← Transformation

REPOSITORY LAYER (UserRepository)
- "Execute SQL INSERT" ← Technical decision
- "Connect to database" ← Database operation
- "Return raw data from DB" ← Data access
```

---

**Spring Annotations sa Service:**

```java
@Service  // Spring component - managed by Spring container
@Transactional  // Auto rollback on error
public class UserServiceImpl implements UserService {
    
    @Autowired  // Inject dependencies
    private UserRepository userRepository;
    
    @Override
    public Student addUser(Student student) {
        // Implementation
    }
}
```

`@Service` = Tells Spring this is a service component
`@Autowired` = Inject the dependency automatically
`@Transactional` = If anything fails, undo everything (ACID compliance)

---

## Current Implementation Analysis - UserServiceImpl

### **The Interface - UserService**

```java
public interface UserService {
    public Student addUser(Student student);
    public List<Student> retrieveAllStudent();
}
```

**What it does:**
- Defines 2 contracts (agreements) na dapat i-implement ng UserServiceImpl
- Says: "Any class implementing me MUST have these 2 methods"

---

### **Current Implementation - UserServiceImpl** (SKELETON ONLY ⚠️)

```java
public class UserServiceImpl implements UserService {
    
    @Override
    public Student addUser(Student student) {
    	return null;  // ❌ EMPTY - Kailangan i-implement
    }

    @Override
    public List<Student> retrieveAllStudent() {
    	return null;  // ❌ EMPTY - Kailangan i-implement
    }
}
```

**Current Problems:**
- ❌ Pareho ay nagre-return lang ng `null` (unusable)
- ❌ Walang actual business logic
- ❌ Walang database interaction (repository)
- ❌ Walang validation ng data
- ❌ Walang `@Service`, `@Autowired`, `@Transactional` annotations
- ❌ Walang error handling

---

### **The Student Model**

```java
public class Student {
    private String firstName;
    private String lastName;
    private float midtermGrade;
    private float finalGrade;
    private long id;
    
    // Constructor
    public Student(long id, String firstName, String lastName,
            float midtermGrade, float finalGrade) { ... }
    
    // Getters & Setters (Encapsulation)
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    // ... more getters/setters
    
    // Business Logic Methods:
    public float compute() {  // Calculate average
        return (midtermGrade + finalGrade) / 2;
    }
    
    public String evaluate() {  // Pass or Fail
        float average = this.compute();
        if (average >= 75) {
            return "Pass";
        } else {
            return "Failed";
        }
    }
}
```

**Student Model has:**
- ✅ Private properties (Encapsulation principle)
- ✅ Constructor for object creation
- ✅ Getters & Setters for controlled access
- ✅ Business logic methods (`compute()`, `evaluate()`)

---

### **Data Flow Through Service Layer**

```
1. CLIENT sends HTTP request
   POST /api/students {"firstName": "Juan", "lastName": "Dela Cruz", ...}
            ↓
2. CONTROLLER receives request
   @PostMapping("/students")
   public ResponseEntity<?> addStudent(@RequestBody Student student) {
            ↓
3. CONTROLLER calls Service
   Student saved = userService.addUser(student);
            ↓
4. SERVICE validates and processes
   - Check if firstName is not empty
   - Check if grades are 0-100
   - Apply business rules
            ↓
5. SERVICE calls Repository
   return studentRepository.save(student);
            ↓
6. REPOSITORY saves to Database
   SQL: INSERT INTO students VALUES (...)
            ↓
7. DATABASE returns saved Student with ID
            ↓
8. SERVICE returns Student to Controller
            ↓
9. CONTROLLER returns JSON response to client
   HTTP 201 CREATED + Student object
```

---

### **What Needs to Be Implemented** ⚙️

#### **Method 1: `addUser(Student student)`**

Should do:
1. **Validate input** - Check if data is not null/empty
2. **Check business rules** - Grades must be 0-100
3. **Check for duplicates** - If applicable
4. **Set defaults** - Status, timestamps
5. **Call repository** - Save to database
6. **Return result** - The saved Student

```java
@Override
public Student addUser(Student student) {
    // 1. VALIDATE
    if (student.getFirstName() == null || student.getFirstName().isEmpty()) {
        throw new ValidationException("First name is required");
    }
    if (student.getLastName() == null || student.getLastName().isEmpty()) {
        throw new ValidationException("Last name is required");
    }
    
    // 2. CHECK business rules
    if (student.getMidtermGrade() < 0 || student.getMidtermGrade() > 100) {
        throw new ValidationException("Midterm grade must be 0-100");
    }
    if (student.getFinalGrade() < 0 || student.getFinalGrade() > 100) {
        throw new ValidationException("Final grade must be 0-100");
    }
    
    // 3. SAVE to database
    Student savedStudent = studentRepository.save(student);
    
    // 4. RETURN
    return savedStudent;
}
```

---

#### **Method 2: `retrieveAllStudent()`**

Should do:
1. **Query database** - Get all students from repository
2. **Filter if needed** - Apply business rules (only active, etc.)
3. **Return list** - All students

```java
@Override
public List<Student> retrieveAllStudent() {
    // 1. GET from repository
    List<Student> students = studentRepository.findAll();
    
    // 2. FILTER if needed (example)
    // Can add filtering logic here
    // e.g., students.stream().filter(...).collect(...)
    
    // 3. RETURN
    return students;
}
```

---

### **Issues with Current Implementation**

| **Issue** | **Impact** | **Solution** |
|---|---|---|
| Returns `null` | Method unusable, causes NullPointerException | Implement actual logic |
| No validation | Invalid data gets saved to DB | Add validation annotations & logic |
| No repository | Can't save/retrieve data | Inject `UserRepository` |
| No annotations | Spring doesn't recognize as service | Add `@Service`, `@Autowired` |
| No error handling | Errors not caught/handled properly | Add try-catch & throw exceptions |
| No business logic | No actual processing happens | Implement business rules |

---

### **Summary: Service Layer Implementation Checklist** ✓

- [ ] Add `@Service` annotation to UserServiceImpl
- [ ] Add `@Autowired UserRepository` dependency
- [ ] Implement `addUser()` with validation & repository call
- [ ] Implement `retrieveAllStudent()` with repository call
- [ ] Add input validation checks
- [ ] Add business rule validation
- [ ] Handle exceptions properly
- [ ] Add logging (optional)

---

### **3. Repository/DAO Layer** (Data Access)
📌 **Responsibility:** Makipag-communicate sa database

**OOP Concept:**
- **Abstraction**: Ang SQL queries ay hinihide sa repository
- **Encapsulation**: Database operations ay isolated

```
Repository (Data Access Object)
    ↓
Database
```

---

## OOP Principles Applied

### **1. Encapsulation**
Ang bawat class ay nag-hide ng internal details at nag-expose lang ng kailangan:

```java
// Model class - attributes ay private, access through getters/setters
public class User {
    private String name;           // Private - hindi direct access
    
    public String getName() {      // Getter - controlled access
        return name;
    }
    
    public void setName(String name) { // Setter - validation possible
        this.name = name;
    }
}
```

**Benefit:** Protektado ang data, kontrolado ang modification

---

### **2. Inheritance**
Controllers at Services ay maaaring mag-extend ng base classes:

```
ResponseEntity (Spring base class)
    ↓
Custom Response wrappers (inheritance example)
```

**OOP Concept:**
- Controllers ay nag-extend ng functionality from Spring's base classes
- Services ay nag-inherit ng common methods

---

### **3. Polymorphism**
Same interface, different implementations:

```java
// Interface
public interface UserService {
    User getUserById(Long id);
}

// Implementation 1
public class UserServiceImpl implements UserService {
    @Override
    public User getUserById(Long id) {
        // Implementation specific logic
    }
}

// Implementation 2 (hypothetical)
public class MockUserService implements UserService {
    @Override
    public User getUserById(Long id) {
        // Mock data for testing
    }
}
```

**Usage:** Controller ay hindi alam kung saan galing ang data (UserServiceImpl o MockUserService)

---

### **4. Abstraction**
Different layers ay abstract sa bawat isa:

```
┌─────────────────────┐
│  Controller Layer   │ ← sees only UserService interface
├─────────────────────┤
│  Service Layer      │ ← doesn't know about HTTP
├─────────────────────┤
│  Repository Layer   │ ← doesn't know about business logic
├─────────────────────┤
│  Database           │ ← just stores data
└─────────────────────┘
```

---

## How Data Flows Through Layers

### **Example: Create User**

```
1. CLIENT REQUEST (HTTP POST)
   POST /users {"name": "John"}
            ↓
2. CONTROLLER (UserRestController)
   @PostMapping
   createUser(@RequestBody User user) → calls userService.save(user)
            ↓
3. SERVICE LAYER (UserServiceImpl)
   - Validates user data
   - Applies business rules
   - Calls repository.save(user)
            ↓
4. REPOSITORY LAYER (UserRepository)
   - Executes SQL INSERT query
            ↓
5. DATABASE
   - Saves data
            ↓
6. RESPONSE (back through same path)
   HTTP 201 CREATED + User object
```

---

## DTO (Data Transfer Object)

**Location:** `src/main/java/com/pup/taguig/app/dto/`

```java
// ActivityRequest.java - DTO
public class ActivityRequest {
    private String name;
    private String description;
    // getters/setters
}

// Vs model:
// Activity.java - has additional fields like id, createdAt
public class Activity {
    private Long id;              // Not needed from client
    private String name;
    private String description;
    private LocalDateTime createdAt; // Auto-generated
}
```

**Why DTO?**
- 🔒 Protektado ang sensitive model fields
- 📤 Kontrolado kung anong fields ang ire-receive
- 🔄 Flexible API contracts

---

## Separation of Concerns (SoC)

| Layer | Concern | Example |
|-------|---------|---------|
| **Controller** | HTTP handling | Receive request, return response |
| **Service** | Business logic | Validation, calculations, rules |
| **Repository** | Data persistence | SQL queries, CRUD operations |
| **Model** | Data structure | Entity definition |

**Benefit:** Easy to test, maintain, at modify each layer independently

---

## Package Organization

```
com.pup.taguig.app
├── controller/      → HTTP requests (REST endpoints)
├── service/         → Business logic
│   └── impl/       → ServiceImpl classes
├── repository/      → Database access
├── model/          → Entity classes (User, Activity, etc.)
├── dto/            → Request/Response objects
└── exception/      → Custom exceptions
```

**Why organize this way?**
- 📁 Easy to locate files
- 🔍 Clear responsibility
- 🚀 Scalable structure
- 👥 Team collaboration friendly

---

## Key Takeaways for OOP

✅ **Encapsulation:** Private attributes, public getters/setters
✅ **Inheritance:** Services inherit from base classes
✅ **Polymorphism:** Interface UserService → UserServiceImpl
✅ **Abstraction:** Each layer abstracts details from other layers
✅ **Single Responsibility:** Controller = HTTP, Service = Logic, Repo = Data

---

## Technologies Used

- **Framework:** Spring Boot 3.2.3
- **Language:** Java 17
- **Build Tool:** Maven
- **Architecture Pattern:** Layered (N-Tier)
- **API Style:** REST (RESTful Web Services)

---

## Next Steps

1. **Add Database Layer** - Replace in-memory lists with actual database (JPA/Hibernate)
2. **Add Authentication** - Spring Security for user authentication
3. **Add Validation** - Bean Validation annotations (@Valid, @NotNull)
4. **Add Error Handling** - Global exception handlers
5. **Add Logging** - SLF4J/Logback for debugging
