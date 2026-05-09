# Database Architecture & Data Flow Guide

> **Project:** appdev (Spring Boot 3.4.4 + MySQL)
> **This doc extends:** `APPDEV_ARCHITECTURE.md` — adds the full database layer
> **Covers:** Repository layer, @Autowired, daloy ng data mula HTTP request hanggang MySQL

---

## Table of Contents
1. [Complete 4-Layer Architecture](#1-complete-4-layer-architecture)
2. [Bakit Kailangan ng Architecture Folders](#2-bakit-kailangan-ng-architecture-folders)
3. [Paano Naka-connect ang Bawat Layer — @Autowired](#3-paano-naka-connect-ang-bawat-layer--autowired)
4. [Repository Layer In-Depth](#4-repository-layer-in-depth)
5. [Two Database Paths: JPA vs MyBatis](#5-two-database-paths-jpa-vs-mybatis)
6. [Full Data Flow — POST (Create)](#6-full-data-flow--post-create)
7. [Full Data Flow — GET (Read)](#7-full-data-flow--get-read)
8. [Full Data Flow — DELETE](#8-full-data-flow--delete)
9. [Dependency Chain Map](#9-dependency-chain-map)
10. [What Happens on Spring Boot Startup](#10-what-happens-on-spring-boot-startup)

---

## 1. Complete 4-Layer Architecture

Ang existing architecture guide ay nagpapakita ng 3 layers. Ito ang complete na picture kasama ang database:

```
┌─────────────────────────────────────────────────────────────────┐
│  CLIENT (Postman / Frontend / AccrediTrack)                     │
│  → sends HTTP requests (GET, POST, PUT, DELETE)                 │
└────────────────────────────┬────────────────────────────────────┘
                             │ HTTP
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│  LAYER 1: CONTROLLER                                            │
│  controller/                                                    │
│  ├── ActivityController.java      @RequestMapping("/activities")│
│  └── UserRestController.java      @RequestMapping("user")       │
│                                                                 │
│  Job: Tanggapin ang HTTP request, i-pass sa Service             │
│       Ibalik ang HTTP response sa client                        │
│  Annotations: @RestController, @GetMapping, @PostMapping, etc.  │
└────────────────────────────┬────────────────────────────────────┘
                             │ Java method call
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│  LAYER 2: SERVICE                                               │
│  service/                                                       │
│  ├── ActivityService.java         (interface — contract)        │
│  ├── UserService.java             (interface — contract)        │
│  ├── StudentService.java          (interface — contract)        │
│  └── impl/                                                      │
│      ├── ActivityServiceImpl.java (actual logic — JPA)          │
│      ├── UserServiceImpl.java     (actual logic — JPA)          │
│      └── StudentServiceImpl.java  (actual logic — MyBatis)      │
│                                                                 │
│  Job: Business logic, validation, data transformation           │
│  Annotations: @Service, @Autowired, @Override                   │
└────────────────────────────┬────────────────────────────────────┘
                             │ Java method call
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│  LAYER 3: REPOSITORY / MAPPER                                   │
│  repository/                  repositoryM/                      │
│  ├── StudentRepository.java   ├── StudentMapper.java (@Mapper)  │
│  ├── ActivityRepository.java  │                                 │
│  └── UserRepository.java      │   + resources/mappers/          │
│  (extends JpaRepository)      │       StudentMapper.xml         │
│                                                                 │
│  Job: I-execute ang database queries                            │
│  JPA: auto-generates SQL    MyBatis: uses your XML SQL          │
│  Annotations: @Repository, @Mapper                              │
└────────────────────────────┬────────────────────────────────────┘
                             │ JDBC / SQL
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│  LAYER 4: DATABASE                                              │
│  MySQL — appdev_db_try                                          │
│  ├── student table      (mapped from Student.java / StudentM)   │
│  ├── activity table     (mapped from Activity.java)             │
│  └── user table         (mapped from User.java)                 │
│                                                                 │
│  Config: application.properties → datasource.url               │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. Bakit Kailangan ng Architecture Folders

Hindi lang ito organization — bawat folder ay may **specific rule** kung ano lang ang dapat nasa loob niya.

```
com.pup.taguig.app/
│
├── controller/         ← HTTP LAYER ONLY
│   │                     Bawal dito: SQL, business logic, DB calls
│   ├── ActivityController.java
│   └── UserRestController.java
│
├── service/            ← LOGIC LAYER ONLY
│   │                     Bawal dito: HTTP annotations (@GetMapping etc.), raw SQL
│   ├── ActivityService.java      (interface)
│   ├── UserService.java          (interface)
│   ├── StudentService.java       (interface)
│   └── impl/
│       ├── ActivityServiceImpl.java
│       ├── UserServiceImpl.java
│       └── StudentServiceImpl.java
│
├── repository/         ← JPA DATA ACCESS LAYER
│   │                     Bawal dito: business logic, HTTP stuff
│   ├── StudentRepository.java    (JpaRepository)
│   ├── ActivityRepository.java   (JpaRepository)
│   └── UserRepository.java       (JpaRepository)
│
├── repositoryM/        ← MYBATIS DATA ACCESS LAYER
│   │                     Katulad ng repository/ pero para sa MyBatis
│   └── StudentMapper.java        (@Mapper)
│
├── model/              ← DATA STRUCTURES (Entities)
│   │                     Ang Java representation ng iyong DB tables
│   ├── Student.java              (@Entity — JPA)
│   ├── Activity.java             (@Entity — JPA)
│   └── StudentM.java             (plain POJO — MyBatis)
│
├── dto/                ← TRANSFER OBJECTS
│   │                     Hindi ito entities — ito ang shapes ng request/response
│   ├── StudentRequestDTO.java    (what client sends)
│   ├── StudentResponseDTO.java   (what client receives)
│   ├── ActivityRequestDTO.java
│   └── ActivityResponseDTO.java
│
└── resources/
    ├── application.properties    ← DATABASE CONNECTION CONFIG
    └── mappers/
        └── StudentMapper.xml     ← MYBATIS SQL QUERIES
```

### Bakit ganito ang separation?

| Folder | Tanong na sinasagot | Bawal |
|---|---|---|
| `controller/` | "Paano ko tatanggapin ang request?" | SQL, business rules |
| `service/` | "Ano ang dapat mangyari sa data?" | HTTP annotations, raw DB calls |
| `repository/` | "Paano ko ise-save/kukuhanin sa DB?" | Validation, HTTP stuff |
| `model/` | "Ano ang hugis ng data sa DB?" | Logic, HTTP stuff |
| `dto/` | "Ano ang papadalahin ko sa client?" | JPA annotations, business logic |

**Real benefit:** Kapag may bug ka sa database query — alam mo agad na nasa `repository/` or `repositoryM/` ito. Hindi mo na kailangang hanapin sa buong codebase.

---

## 3. Paano Naka-connect ang Bawat Layer — @Autowired

Ang `@Autowired` ang siyang nag-iingat ng lahat ng connection sa pagitan ng layers. Ito ang **Dependency Injection** ng Spring.

### Paano gumagana ang @Autowired

```java
// UserServiceImpl.java
@Service
public class UserServiceImpl implements UserService {

    @Autowired                          // ← Spring, bigyan mo ako ng StudentRepository
    private StudentRepository studentRepository;

    // Ngayon pwede na akong gumamit ng studentRepository anywhere dito
}
```

**Ano ang ginagawa ng Spring:**

```
Startup ng Spring Boot
    ↓
Spring scans all classes with @Service, @Repository, @Controller, @Mapper
    ↓
Spring creates ONE instance of each (called a "bean")
    ↓
Kapag nakakita ng @Autowired, ino-inject niya ang tamang bean
    ↓
Result: Connected na ang lahat ng layers
```

Parang plug and socket — `@Autowired` ang plug, at Spring ang socket na nagbibigay ng kuryente (instance).

### Full Autowired Chain

```
UserRestController
    @Autowired UserService userService          ← gets UserServiceImpl
    @Autowired StudentService studentService    ← gets StudentServiceImpl

UserServiceImpl (@Service)
    @Autowired StudentRepository studentRepository  ← gets JPA repo

StudentServiceImpl (@Service)
    @Autowired StudentMapper studentMapper          ← gets MyBatis mapper

ActivityServiceImpl (@Service)
    @Autowired ActivityRepository activityRepository ← gets JPA repo
```

### Bakit hindi kayo nag-new() ng objects?

```java
// HINDI GINAGAWA (manual instantiation):
StudentRepository repo = new StudentRepository();   // ← MALI

// GINAGAWA (Spring injection):
@Autowired
private StudentRepository studentRepository;        // ← TAMA
```

Kapag ikaw mismo ang nag-`new()`, ikaw ang responsable sa lifecycle niya. Si Spring naman, siya ang nag-aasikaso ng:
- Kailan ito gagawin
- Isa lang ba o maraming instance
- Paano ito mide-destroy
- Ano pa ang ino-inject sa loob nito

---

## 4. Repository Layer In-Depth

### JPA Repositories — StudentRepository, ActivityRepository, UserRepository

```java
// repository/StudentRepository.java
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByFirstNameAndLastName(String firstName, String name);
}

// repository/ActivityRepository.java
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
}

// repository/UserRepository.java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
```

**Tandaan:** Interface lang ito — walang `implements`, walang `{}` method body para sa built-in methods. Si Spring Data JPA ang nag-iimplement sa runtime.

### Lahat ng libre mong makukuha mula sa JpaRepository

```java
// Automatically available — hindi mo kailangang i-code pa:

studentRepository.save(student)          // INSERT or UPDATE
studentRepository.findAll()              // SELECT * FROM student
studentRepository.findById(1L)           // SELECT * WHERE id = 1  → returns Optional<Student>
studentRepository.existsById(1L)         // SELECT COUNT(*) WHERE id = 1  → boolean
studentRepository.deleteById(1L)         // DELETE WHERE id = 1
studentRepository.count()               // SELECT COUNT(*) FROM student
```

### Custom Query — findByFirstNameAndLastName

```java
// Ito na lang ang idinagdag — wala pang implementation, derivation lang
List<Student> findByFirstNameAndLastName(String firstName, String name);
```

```
Spring reads the method name:
findBy  →  WHERE
FirstName  →  first_name = ?
And  →  AND
LastName  →  last_name = ?

Generated SQL:
SELECT * FROM student WHERE first_name = ? AND last_name = ?
```

### MyBatis Mapper — StudentMapper

```java
// repositoryM/StudentMapper.java
@Mapper
public interface StudentMapper {
    public StudentM getUserById(Long id);
    public List<StudentM> retrieveAllStudent();
}
```

Hindi `extends JpaRepository` — kaya kailangan ng XML file para sa SQL.

```
StudentMapper.java method name  →  must match id in StudentMapper.xml
getUserById(Long id)            →  <select id="getUserById">
retrieveAllStudent()            →  <select id="retrieveAllStudent">  ← wala pa sa XML!
```

```xml
<!-- resources/mappers/StudentMapper.xml -->
<mapper namespace="com.pup.taguig.app.repositoryM.StudentMapper">

    <resultMap id="studentResultMap" type="com.pup.taguig.app.model.StudentM">
        <id     column="id"             property="id"/>
        <result column="first_name"     property="firstName"/>
        <result column="last_name"      property="lastName"/>
        <result column="midterm_grade"  property="midtermGrade"/>
        <result column="final_grade"    property="finalGrade"/>
    </resultMap>

    <select id="getUserById" resultMap="studentResultMap">
        SELECT * FROM student WHERE id = #{id}
    </select>

</mapper>
```

---

## 5. Two Database Paths: JPA vs MyBatis

Sa ating project, dalawang paraan ng pagkonekta sa database:

```
                        UserRestController
                        /               \
          (JPA path)   /                 \  (MyBatis path)
                      /                   \
            UserServiceImpl          StudentServiceImpl
                  |                        |
         StudentRepository           StudentMapper
         (JpaRepository)             (@Mapper + XML)
                  |                        |
            Hibernate                  MyBatis
                  |                        |
                  └──────────┬─────────────┘
                             |
                           MySQL
                      (appdev_db_try)
                       student table
```

Parehong nakakonekta sa **iisang MySQL database** at **iisang `student` table**.

---

## 6. Full Data Flow — POST (Create)

### `POST /user/` — Mag-add ng bagong Student (JPA path)

```
CLIENT sends:
POST http://localhost:8080/user/
Body: {
    "firstName": "Annie",
    "lastName": "Raquem",
    "midtermGrade": 85,
    "finalGrade": 90
}
```

```
STEP 1: HTTP hits UserRestController.addStudent()
────────────────────────────────────────────────────────
@PostMapping("/")
public Long addStudent(@RequestBody StudentRequestDTO student) {
    Long result = null;
    if (Objects.nonNull(student)) {
        result = userService.addUser(student);    // ← pass to service
    }
    return result;
}

   ↓ userService.addUser(student)


STEP 2: UserServiceImpl.addUser() executes
────────────────────────────────────────────────────────
@Override
public Long addUser(StudentRequestDTO request) {
    Student student = new Student(
        request.getFirstName(),    // "Annie"
        request.getLastName(),     // "Raquem"
        request.getMidtermGrade(), // 85
        request.getFinalGrade()    // 90
    );

    student = studentRepository.save(student);    // ← pass to JPA
    return student.getId();
}

   ↓ studentRepository.save(student)


STEP 3: JPA (Hibernate) generates and executes SQL
────────────────────────────────────────────────────────
Hibernate SQL (visible in console because show-sql=true):

INSERT INTO student (first_name, last_name, midterm_grade, final_grade)
VALUES ('Annie', 'Raquem', 85.0, 90.0)

   ↓ MySQL executes INSERT, assigns id = 1 (AUTO_INCREMENT)


STEP 4: Return trip back up
────────────────────────────────────────────────────────
MySQL        → returns saved row with id = 1
Hibernate    → maps row back to Student object { id: 1, firstName: "Annie", ... }
Repository   → returns Student to UserServiceImpl
Service      → returns student.getId() = 1L to Controller
Controller   → returns 1L as HTTP response body

CLIENT receives: 1
```

---

## 7. Full Data Flow — GET (Read)

### `GET /user/1` — Kuhanin ang Student by ID (MyBatis path)

```
CLIENT sends:
GET http://localhost:8080/user/1
```

```
STEP 1: HTTP hits UserRestController.getUsersById()
────────────────────────────────────────────────────────
@GetMapping("/{id}")
public StudentResponseDTO getUsersById(@PathVariable Long id) {
    return studentService.getUserById(id);    // ← StudentServiceImpl (MyBatis)
}

   ↓ studentService.getUserById(1L)


STEP 2: StudentServiceImpl.getUserById() executes
────────────────────────────────────────────────────────
@Override
public StudentResponseDTO getUserById(Long id) {
    StudentM student = studentMapper.getUserById(id);    // ← MyBatis mapper
    return new StudentResponseDTO(
        student.getId(), student.getFirstName(), ...
    );
}

   ↓ studentMapper.getUserById(1L)


STEP 3: MyBatis looks up StudentMapper.xml
────────────────────────────────────────────────────────
Finds:  namespace = "com.pup.taguig.app.repositoryM.StudentMapper"
        id = "getUserById"

Executes SQL:
SELECT * FROM student WHERE id = 1


STEP 4: MySQL returns the row
────────────────────────────────────────────────────────
Row: { id: 1, first_name: "Annie", last_name: "Raquem",
       midterm_grade: 85.0, final_grade: 90.0 }


STEP 5: MyBatis applies resultMap
────────────────────────────────────────────────────────
first_name   →  firstName     (snake_case → camelCase)
last_name    →  lastName
midterm_grade → midtermGrade
final_grade  →  finalGrade

Creates StudentM object with mapped values.


STEP 6: Return trip
────────────────────────────────────────────────────────
StudentM object → StudentServiceImpl
StudentServiceImpl wraps it in StudentResponseDTO
Controller returns StudentResponseDTO as JSON

CLIENT receives:
{
    "id": 1,
    "firstName": "Annie",
    "lastName": "Raquem",
    "midtermGrade": 85.0,
    "finalGrade": 90.0
}
```

### `GET /user/` — Kuhanin lahat (JPA path via UserServiceImpl)

```
GET http://localhost:8080/user/
    ↓
UserRestController.getAllUsers()
    ↓ userService.retrieveAllStudent()
UserServiceImpl
    ↓ studentRepository.findAll()
JPA/Hibernate: SELECT * FROM student
    ↓ MySQL returns all rows
    ↓ Hibernate maps each row → Student object
    ↓ List<Student>
UserServiceImpl converts each Student → StudentResponseDTO
    ↓ List<StudentResponseDTO>
Controller returns JSON array to client
```

---

## 8. Full Data Flow — DELETE

### `DELETE /user/1` — Burahin ang Student (JPA path)

```
DELETE http://localhost:8080/user/1
    ↓
UserRestController.deleteStudent(1L)
    ↓ userService.deleteStudent(1L)
UserServiceImpl:
    if (studentRepository.existsById(1L)) {    // SELECT COUNT(*) WHERE id = 1 → true
        studentRepository.deleteById(1L);       // DELETE FROM student WHERE id = 1
        return true;
    }
    return false;
    ↓
Controller returns: true
```

---

## 9. Dependency Chain Map

Ito ang buong dependency tree ng ating project — sino ang may `@Autowired` sa sino:

```
Spring Boot Application
    │
    ├── UserRestController  (@RestController)
    │       │
    │       ├── @Autowired UserService → UserServiceImpl
    │       │                               │
    │       │                               └── @Autowired StudentRepository
    │       │                                   (JpaRepository<Student, Long>)
    │       │                                       └── Hibernate → MySQL
    │       │
    │       ├── @Autowired StudentService → StudentServiceImpl
    │       │                               │
    │       │                               └── @Autowired StudentMapper
    │       │                                   (@Mapper interface)
    │       │                                       └── StudentMapper.xml → MySQL
    │       │
    │       └── StudentController (injected via constructor)
    │
    └── ActivityController  (@RestController)
            │
            └── @Autowired ActivityService → ActivityServiceImpl
                                            │
                                            └── @Autowired ActivityRepository
                                                (JpaRepository<Activity, Long>)
                                                    └── Hibernate → MySQL
```

### Connections established by application.properties

```properties
# This is what connects ALL the repositories to MySQL:
spring.datasource.url=jdbc:mysql://localhost:3306/appdev_db_try
spring.datasource.username=niera
spring.datasource.password=appdevpass123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Tells JPA/Hibernate to auto-update schema from @Entity classes
spring.jpa.hibernate.ddl-auto=update

# Tells MyBatis where to find XML SQL files
mybatis.mapper-locations=classpath:mappers/*.xml
```

---

## 10. What Happens on Spring Boot Startup

```
1. SpringBootWeb1Application.main() runs
       ↓
2. Spring Boot reads application.properties
   - Connects to MySQL: jdbc:mysql://localhost:3306/appdev_db_try
   - Configures JPA/Hibernate
   - Configures MyBatis mapper locations
       ↓
3. Hibernate checks @Entity classes (Student, Activity, User)
   spring.jpa.hibernate.ddl-auto=update:
   - Kung walang table → CREATE TABLE
   - Kung may bago field → ALTER TABLE (add column)
   - Hindi nag-drop ng existing data
       ↓
4. MyBatis scans for @Mapper interfaces (StudentMapper)
   - Reads StudentMapper.xml
   - Links XML queries to Java interface methods
       ↓
5. Spring component scan runs — finds all:
   @RestController → creates beans: UserRestController, ActivityController
   @Service        → creates beans: UserServiceImpl, ActivityServiceImpl, StudentServiceImpl
   @Repository     → creates beans: StudentRepository, ActivityRepository, UserRepository
   @Mapper         → creates beans: StudentMapper
       ↓
6. @Autowired injections are resolved:
   UserRestController gets: UserServiceImpl, StudentServiceImpl
   UserServiceImpl gets: StudentRepository
   ActivityServiceImpl gets: ActivityRepository
   StudentServiceImpl gets: StudentMapper
       ↓
7. @PostConstruct runs in UserRestController:
   - Initializes in-memory students list (Annie, Rose, Yowro, Ann, Niera)
   - Note: hindi ito stored sa DB — sa memory lang ng app
       ↓
8. Embedded Tomcat server starts on port 8080
   "Tomcat started on port 8080"
       ↓
9. Application ready — naghihintay na ng HTTP requests
```

---

## Quick Reference: Which file handles what?

| You want to... | Go to this file |
|---|---|
| Add a new API endpoint | `controller/` |
| Change validation or business logic | `service/impl/` |
| Add a new database query (JPA) | `repository/` — add method using naming convention |
| Add a new database query (MyBatis) | `repositoryM/StudentMapper.java` + `mappers/StudentMapper.xml` |
| Change what fields are in the DB table | `model/` — modify @Entity class |
| Change what client sends/receives | `dto/` — modify DTO class |
| Change DB connection / credentials | `resources/application.properties` |
| Add a new Maven dependency | `pom.xml` |
