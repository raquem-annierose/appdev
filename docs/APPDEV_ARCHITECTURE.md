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
📌 **Responsibility:** Gawin ang actual business logic at processing

**OOP Concept:**
- **Polymorphism**: UserService (interface) → UserServiceImpl (implementation)
- **Abstraction**: Ang database queries ay abstracto mula sa controller

```
Service Interface (UserService)
    ↓
Service Implementation (UserServiceImpl)
    ↓
Repository Layer
```

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
