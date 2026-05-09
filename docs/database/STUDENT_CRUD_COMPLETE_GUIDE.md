# Student / User — Complete CRUD Reference

> **Project:** appdev (Spring Boot 3.4.4 + MySQL)
> **Base URL:** `http://localhost:8080/user`
> **Covers:** Every file, every layer, every line of code used in the User/Student CRUD feature

---

## Table of Contents
1. [Complete File Hierarchy](#1-complete-file-hierarchy)
2. [Layer 0: Database Config](#2-layer-0-database-config)
3. [Layer 1: Model](#3-layer-1-model)
4. [Layer 2: DTO](#4-layer-2-dto)
5. [Layer 3: Repository](#5-layer-3-repository)
6. [Layer 4: Service Interface](#6-layer-4-service-interface)
7. [Layer 5: Service Implementation (JPA)](#7-layer-5-service-implementation-jpa)
8. [Layer 5b: Service Implementation (MyBatis)](#8-layer-5b-service-implementation-mybatis)
9. [Layer 6: Controller](#9-layer-6-controller)
10. [CRUD Operations — Full Code Trace](#10-crud-operations--full-code-trace)
    - [CREATE — POST /user/](#create--post-user)
    - [READ ALL — GET /user/](#read-all--get-user)
    - [READ ONE — GET /user/{id}](#read-one--get-userid)
    - [SEARCH — GET /user/filter](#search--get-userfilter)
    - [DELETE — DELETE /user/{id}](#delete--delete-userid)
11. [Note: Legacy In-Memory Controller](#11-note-legacy-in-memory-controller)

---

## 1. Complete File Hierarchy

```
Files directly involved in Student/User CRUD:

src/main/java/com/pup/taguig/app/
│
├── controller/
│   ├── UserRestController.java       ← PRIMARY: all /user endpoints
│   └── StudentController.java        ← LEGACY: /api/students (in-memory, no DB)
│
├── service/
│   ├── UserService.java              ← JPA service interface
│   ├── StudentService.java           ← MyBatis service interface
│   └── impl/
│       ├── UserServiceImpl.java      ← JPA implementation (main CRUD)
│       └── StudentServiceImpl.java   ← MyBatis implementation (GET only)
│
├── repository/
│   ├── StudentRepository.java        ← JPA repository (extends JpaRepository)
│   └── UserRepository.java           ← JPA repository for User model (exists, unused in active CRUD)
│
├── repositoryM/
│   └── StudentMapper.java            ← MyBatis mapper interface
│
├── model/
│   ├── Student.java                  ← @Entity mapped to `student` table (JPA)
│   ├── StudentM.java                 ← Plain POJO for MyBatis (no JPA annotations)
│   └── User.java                     ← @Entity mapped to `user` table (unused in active CRUD)
│
└── dto/
    ├── StudentRequestDTO.java         ← Shape of request body from client
    └── StudentResponseDTO.java        ← Shape of response body to client

src/main/resources/
├── application.properties            ← DB connection + JPA + MyBatis config
└── mappers/
    └── StudentMapper.xml             ← MyBatis SQL queries
```

---

## 2. Layer 0: Database Config

**File:** `src/main/resources/application.properties`

```properties
spring.application.name=demo
server.port=8080

# MySQL Connection
spring.datasource.url=jdbc:mysql://localhost:3306/appdev_db_try
spring.datasource.username=niera
spring.datasource.password=appdevpass123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# MyBatis config
mybatis.mapper-locations=classpath:mappers/*.xml
mybatis.type-aliases-package=com.pup.taguig.app.model
```

**What each line does:**

| Line | Purpose |
|---|---|
| `datasource.url` | Connects to `appdev_db_try` database on localhost port 3306 |
| `datasource.username / password` | MySQL login credentials |
| `driver-class-name` | MySQL JDBC driver class |
| `ddl-auto=update` | Hibernate auto-syncs table schema with `@Entity` classes on startup |
| `show-sql=true` | Prints generated SQL queries in the console |
| `format_sql=true` | Makes the printed SQL human-readable |
| `mapper-locations` | Tells MyBatis where to find XML SQL files |
| `type-aliases-package` | So MyBatis can reference `StudentM` without the full package name |

**pom.xml dependencies used:**

```xml
<!-- JPA + Hibernate -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- MySQL JDBC Driver -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- MyBatis -->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>3.0.3</version>
</dependency>
```

---

## 3. Layer 1: Model

### Student.java — JPA Entity (used by UserServiceImpl)

**File:** `src/main/java/com/pup/taguig/app/model/Student.java`

```java
package com.pup.taguig.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 100, unique = true)
    private String firstName;

    @Column(nullable = false, insertable = true, updatable = false)
    private String lastName;

    @Column(nullable = true)
    private float midtermGrade;

    @Column(nullable = true)
    private float finalGrade;

    public Student() {}

    public Student(String firstName, String lastName, float midtermGrade, float finalGrade) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.midtermGrade = midtermGrade;
        this.finalGrade = finalGrade;
    }

    public Long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public float getMidtermGrade() { return midtermGrade; }
    public void setMidtermGrade(float midtermGrade) { this.midtermGrade = midtermGrade; }

    public float getFinalGrade() { return finalGrade; }
    public void setFinalGrade(float finalGrade) { this.finalGrade = finalGrade; }

    public float compute() {
        return (midtermGrade + finalGrade) / 2;
    }

    public String evaluate() {
        float average = this.compute();
        if (average >= 75) return "Pass";
        else return "Failed";
    }
}
```

**Annotation Breakdown:**

| Annotation | Effect on `student` table |
|---|---|
| `@Entity` | Maps this class to a DB table named `student` |
| `@Id` | `id` column = primary key |
| `@GeneratedValue(IDENTITY)` | id = AUTO_INCREMENT in MySQL |
| `@Column(nullable=false, length=100, unique=true)` | `first_name VARCHAR(100) NOT NULL UNIQUE` |
| `@Column(nullable=false, updatable=false)` | `last_name NOT NULL`, cannot be updated after insert |
| `@Column(nullable=true)` | `midterm_grade / final_grade` — optional columns |

**Generated MySQL table:**
```sql
CREATE TABLE student (
    id             BIGINT       AUTO_INCREMENT PRIMARY KEY,
    first_name     VARCHAR(100) NOT NULL UNIQUE,
    last_name      VARCHAR(255) NOT NULL,
    midterm_grade  FLOAT,
    final_grade    FLOAT
);
```

---

### StudentM.java — MyBatis POJO (used by StudentServiceImpl)

**File:** `src/main/java/com/pup/taguig/app/model/StudentM.java`

```java
package com.pup.taguig.app.model;

public class StudentM {

    private long id;
    private String firstName;
    private String lastName;
    private float midtermGrade;
    private float finalGrade;

    public StudentM() {}

    public StudentM(String firstName, String lastName, float midtermGrade, float finalGrade) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.midtermGrade = midtermGrade;
        this.finalGrade = finalGrade;
    }

    public Long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public float getMidtermGrade() { return midtermGrade; }
    public void setMidtermGrade(float midtermGrade) { this.midtermGrade = midtermGrade; }

    public float getFinalGrade() { return finalGrade; }
    public void setFinalGrade(float finalGrade) { this.finalGrade = finalGrade; }

    public float compute() { return (midtermGrade + finalGrade) / 2; }

    public String evaluate() {
        float average = this.compute();
        if (average >= 75) return "Pass";
        else return "Failed";
    }
}
```

**Key difference vs Student.java:**
- No `@Entity`, `@Id`, `@Column` — plain Java class
- Reads from the **same `student` table** — mapping handled by `StudentMapper.xml`

---

### User.java — JPA Entity (separate, not used in active Student CRUD)

**File:** `src/main/java/com/pup/taguig/app/model/User.java`

```java
package com.pup.taguig.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private int age;

    public User() {}

    public User(String firstName, String lastName, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    @Override
    public String toString() {
        return "User [First Name=" + firstName + ", Last Name=" + lastName + ", Age=" + age + "]";
    }
}
```

> Note: `User.java` and `UserRepository.java` exist but hindi ginagamit sa active CRUD ng `/user` endpoints. Ang `UserRestController` ay gumagamit ng `Student` model — confusing ang naming but `UserRestController` = Student CRUD.

---

## 4. Layer 2: DTO

### StudentRequestDTO.java — What the client sends

**File:** `src/main/java/com/pup/taguig/app/dto/StudentRequestDTO.java`

```java
package com.pup.taguig.app.dto;

public class StudentRequestDTO {
    private long id;
    private String firstName;
    private String lastName;
    private float midtermGrade;
    private float finalGrade;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public float getMidtermGrade() { return midtermGrade; }
    public void setMidtermGrade(float midtermGrade) { this.midtermGrade = midtermGrade; }

    public float getFinalGrade() { return finalGrade; }
    public void setFinalGrade(float finalGrade) { this.finalGrade = finalGrade; }
}
```

**Used by:** `@RequestBody StudentRequestDTO student` in `UserRestController.addStudent()`

---

### StudentResponseDTO.java — What the client receives

**File:** `src/main/java/com/pup/taguig/app/dto/StudentResponseDTO.java`

```java
package com.pup.taguig.app.dto;

public class StudentResponseDTO {
    private long id;
    private String firstName;
    private String lastName;
    private float midtermGrade;
    private float finalGrade;

    public StudentResponseDTO() {}

    public StudentResponseDTO(Long id, String firstName, String lastName,
                               float midtermGrade, float finalGrade) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.midtermGrade = midtermGrade;
        this.finalGrade = finalGrade;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public float getMidtermGrade() { return midtermGrade; }
    public void setMidtermGrade(float midtermGrade) { this.midtermGrade = midtermGrade; }

    public float getFinalGrade() { return finalGrade; }
    public void setFinalGrade(float finalGrade) { this.finalGrade = finalGrade; }
}
```

**Used by:** Return type of `getAllUsers()`, `getUsersById()`, `searchByName()`

---

### Why DTO instead of raw Student?

```
Client sends JSON
    ↓
@RequestBody StudentRequestDTO   ← only accepts: firstName, lastName, midtermGrade, finalGrade
                                    client CANNOT set the id (auto-generated by DB)

Service creates Student entity from DTO
    ↓
Saves to DB via repository
    ↓
Returns StudentResponseDTO       ← same fields, but now includes the DB-generated id
```

---

## 5. Layer 3: Repository

### StudentRepository.java — JPA (used by UserServiceImpl)

**File:** `src/main/java/com/pup/taguig/app/repository/StudentRepository.java`

```java
package com.pup.taguig.app.repository;

import com.pup.taguig.app.model.Student;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByFirstNameAndLastName(String firstName, String name);
}
```

**What `extends JpaRepository<Student, Long>` gives you for free:**

```java
studentRepository.save(student)         // INSERT or UPDATE
studentRepository.findAll()             // SELECT * FROM student
studentRepository.findById(id)          // SELECT * FROM student WHERE id = ? → Optional<Student>
studentRepository.existsById(id)        // SELECT COUNT(*) WHERE id = ?  → boolean
studentRepository.deleteById(id)        // DELETE FROM student WHERE id = ?
```

**Custom query — Spring Data derives SQL from method name:**

```java
findByFirstNameAndLastName(String firstName, String name)
↓
SELECT * FROM student WHERE first_name = ? AND last_name = ?
```

---

### StudentMapper.java — MyBatis (used by StudentServiceImpl)

**File:** `src/main/java/com/pup/taguig/app/repositoryM/StudentMapper.java`

```java
package com.pup.taguig.app.repositoryM;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import com.pup.taguig.app.model.StudentM;

@Mapper
public interface StudentMapper {

    public StudentM getUserById(Long id);
    public List<StudentM> retrieveAllStudent();
}
```

**Linked to:** `src/main/resources/mappers/StudentMapper.xml`

---

### StudentMapper.xml — MyBatis SQL

**File:** `src/main/resources/mappers/StudentMapper.xml`

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

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

**How linking works:**

```
namespace="com.pup.taguig.app.repositoryM.StudentMapper"
    → links this XML to StudentMapper.java interface

<select id="getUserById">
    → matches StudentMapper.getUserById(Long id) method name exactly

#{id}
    → the Long id parameter from the Java method (prepared statement — SQL injection safe)

resultMap="studentResultMap"
    → maps snake_case DB columns to camelCase Java fields in StudentM:
       first_name   →  firstName
       last_name    →  lastName
       midterm_grade → midtermGrade
       final_grade  →  finalGrade
```

> Note: `retrieveAllStudent()` is declared in the interface but has no matching `<select>` in the XML yet.

---

### UserRepository.java — JPA (exists, not used in active CRUD)

**File:** `src/main/java/com/pup/taguig/app/repository/UserRepository.java`

```java
package com.pup.taguig.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.pup.taguig.app.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
```

> Exists but walang endpoint na gumagamit nito sa ngayon.

---

## 6. Layer 4: Service Interface

### UserService.java — Contract for JPA path

**File:** `src/main/java/com/pup/taguig/app/service/UserService.java`

```java
package com.pup.taguig.app.service;

import java.util.List;
import com.pup.taguig.app.dto.StudentRequestDTO;
import com.pup.taguig.app.dto.StudentResponseDTO;
import com.pup.taguig.app.model.Student;

public interface UserService {

    public Long addUser(StudentRequestDTO student);
    public List<StudentResponseDTO> retrieveAllStudent();
    public StudentResponseDTO getUserById(Long id);
    public List<StudentResponseDTO> searchByName(String name, String firstName);
    public boolean deleteStudent(Long id);
}
```

**Each method maps to an endpoint:**

| Interface Method | HTTP Endpoint |
|---|---|
| `addUser()` | `POST /user/` |
| `retrieveAllStudent()` | `GET /user/` |
| `getUserById()` | (not currently used in UserRestController for JPA path) |
| `searchByName()` | `GET /user/filter?lastName=&firstName=` |
| `deleteStudent()` | `DELETE /user/{id}` |

---

### StudentService.java — Contract for MyBatis path

**File:** `src/main/java/com/pup/taguig/app/service/StudentService.java`

```java
package com.pup.taguig.app.service;

import java.util.List;
import com.pup.taguig.app.dto.StudentResponseDTO;

public interface StudentService {

    public StudentResponseDTO getUserById(Long id);
    public List<StudentResponseDTO> retrieveAllStudent();
}
```

**Maps to:**

| Interface Method | HTTP Endpoint |
|---|---|
| `getUserById()` | `GET /user/{id}` (MyBatis path) |
| `retrieveAllStudent()` | (exists but not used by controller yet) |

---

## 7. Layer 5: Service Implementation (JPA)

**File:** `src/main/java/com/pup/taguig/app/service/impl/UserServiceImpl.java`

```java
package com.pup.taguig.app.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pup.taguig.app.dto.StudentRequestDTO;
import com.pup.taguig.app.dto.StudentResponseDTO;
import com.pup.taguig.app.model.Student;
import com.pup.taguig.app.repository.StudentRepository;
import com.pup.taguig.app.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private StudentRepository studentRepository;

    // ── CREATE ──────────────────────────────────────────────────────────
    @Override
    public Long addUser(StudentRequestDTO request) {
        Student student = new Student(
            request.getFirstName(),
            request.getLastName(),
            request.getMidtermGrade(),
            request.getFinalGrade()
        );
        student = studentRepository.save(student);  // INSERT → returns saved entity with id
        return student.getId();
    }

    // ── READ ALL ─────────────────────────────────────────────────────────
    @Override
    public List<StudentResponseDTO> retrieveAllStudent() {
        List<Student> students = studentRepository.findAll();  // SELECT *
        List<StudentResponseDTO> response = new ArrayList<>();
        for (Student student : students) {
            StudentResponseDTO dto = new StudentResponseDTO(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getMidtermGrade(),
                student.getFinalGrade()
            );
            response.add(dto);
        }
        return response;
    }

    // ── READ ONE ─────────────────────────────────────────────────────────
    @Override
    public StudentResponseDTO getUserById(Long id) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Student not found"));
        return new StudentResponseDTO(
            student.getId(),
            student.getFirstName(),
            student.getLastName(),
            student.getMidtermGrade(),
            student.getFinalGrade()
        );
    }

    // ── SEARCH ───────────────────────────────────────────────────────────
    @Override
    public List<StudentResponseDTO> searchByName(String name, String firstName) {
        List<Student> students = studentRepository.findByFirstNameAndLastName(firstName, name);
        return students.stream()
            .map(student -> new StudentResponseDTO(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getMidtermGrade(),
                student.getFinalGrade()
            ))
            .toList();
    }

    // ── DELETE ───────────────────────────────────────────────────────────
    @Override
    public boolean deleteStudent(Long id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
```

---

## 8. Layer 5b: Service Implementation (MyBatis)

**File:** `src/main/java/com/pup/taguig/app/service/impl/StudentServiceImpl.java`

```java
package com.pup.taguig.app.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pup.taguig.app.dto.StudentResponseDTO;
import com.pup.taguig.app.model.StudentM;
import com.pup.taguig.app.repositoryM.StudentMapper;
import com.pup.taguig.app.service.StudentService;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;

    // ── READ ONE (MyBatis) ───────────────────────────────────────────────
    @Override
    public StudentResponseDTO getUserById(Long id) {
        StudentM student = studentMapper.getUserById(id);  // → runs XML SELECT query
        return new StudentResponseDTO(
            student.getId(),
            student.getFirstName(),
            student.getLastName(),
            student.getMidtermGrade(),
            student.getFinalGrade()
        );
    }

    // ── READ ALL (MyBatis) ───────────────────────────────────────────────
    @Override
    public List<StudentResponseDTO> retrieveAllStudent() {
        List<StudentM> students = studentMapper.retrieveAllStudent();
        return students.stream()
            .map(student -> new StudentResponseDTO(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getMidtermGrade(),
                student.getFinalGrade()
            ))
            .toList();
    }
}
```

---

## 9. Layer 6: Controller

**File:** `src/main/java/com/pup/taguig/app/controller/UserRestController.java`

```java
package com.pup.taguig.app.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.pup.taguig.app.dto.StudentRequestDTO;
import com.pup.taguig.app.dto.StudentResponseDTO;
import com.pup.taguig.app.model.Student;
import com.pup.taguig.app.model.User;
import com.pup.taguig.app.service.StudentService;
import com.pup.taguig.app.service.UserService;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("user")
public class UserRestController {

    private final StudentController studentController;

    private List<Student> students = null;     // in-memory list (legacy, for @PostConstruct only)
    List<User> users = new ArrayList<User>();  // unused

    @Autowired
    private UserService userService;           // → UserServiceImpl (JPA)

    @Autowired
    private StudentService studentService;     // → StudentServiceImpl (MyBatis)

    UserRestController(StudentController studentController) {
        this.studentController = studentController;
    }

    @PostConstruct
    public void init() {
        students = new ArrayList<>();
        // Pre-loads 5 Student objects in memory — NOT saved to DB
        Student st1 = new Student("Annie", "Raquem", 85, 90);
        Student st2 = new Student("Rose", "Raquem", 95, 97);
        Student st3 = new Student("Yowro", "Raquem", 90, 85);
        Student st4 = new Student("Ann", "Raquem", 90, 85);
        Student st5 = new Student("Niera", "Raquem", 90, 85);
        students.add(st1);
        students.add(st2);
        students.add(st3);
        students.add(st4);
        students.add(st5);
    }

    // ── CREATE ──────────────────────────────────────────────────────────
    @PostMapping("/")
    public Long addStudent(@RequestBody StudentRequestDTO student) {
        Long result = null;
        if (Objects.nonNull(student)) {
            result = userService.addUser(student);
        }
        return result;
    }

    // ── READ ALL ─────────────────────────────────────────────────────────
    @GetMapping("/")
    public List<StudentResponseDTO> getAllUsers() {
        return userService.retrieveAllStudent();
    }

    // ── READ ONE — MyBatis path ──────────────────────────────────────────
    @GetMapping("/{id}")
    public StudentResponseDTO getUsersById(@PathVariable Long id) {
        return studentService.getUserById(id);   // ← StudentServiceImpl (MyBatis)
    }

    // ── SEARCH ───────────────────────────────────────────────────────────
    @GetMapping("/filter")
    public List<StudentResponseDTO> searchByName(
            @RequestParam("lastName") String name,
            @RequestParam String firstName) {
        List<StudentResponseDTO> result = new ArrayList<>();
        if (Objects.nonNull(name) && Objects.nonNull(firstName)) {
            result = userService.searchByName(name, firstName);
        }
        return result;
    }

    // ── DELETE ───────────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public boolean deleteStudent(@PathVariable Long id) {
        return userService.deleteStudent(id);
    }
}
```

---

## 10. CRUD Operations — Full Code Trace

---

### CREATE — POST /user/

```
Request:
  POST http://localhost:8080/user/
  Content-Type: application/json
  Body: { "firstName": "Annie", "lastName": "Raquem", "midtermGrade": 85, "finalGrade": 90 }

Response:
  1   ← (the auto-generated DB id)
```

**Code path:**

```
1. UserRestController.addStudent(@RequestBody StudentRequestDTO student)
   │  Objects.nonNull(student) → true
   └─ userService.addUser(student)
                   │  ↓ UserServiceImpl.addUser()
                   │
                   │  new Student("Annie", "Raquem", 85, 90)
                   │
                   └─ studentRepository.save(student)
                                        │  ↓ Hibernate generates:
                                        │
                                        │  INSERT INTO student
                                        │    (first_name, last_name, midterm_grade, final_grade)
                                        │  VALUES
                                        │    ('Annie', 'Raquem', 85.0, 90.0)
                                        │
                                        │  MySQL assigns id = 1 (AUTO_INCREMENT)
                                        │  Returns saved Student { id: 1, ... }
                                        │
                   student.getId() → 1L
                   │
   returns 1L to controller
   │
   HTTP Response: 1
```

---

### READ ALL — GET /user/

```
Request:
  GET http://localhost:8080/user/

Response:
  [
    { "id": 1, "firstName": "Annie", "lastName": "Raquem", "midtermGrade": 85.0, "finalGrade": 90.0 },
    { "id": 2, "firstName": "Rose",  "lastName": "Raquem", "midtermGrade": 95.0, "finalGrade": 97.0 }
  ]
```

**Code path:**

```
1. UserRestController.getAllUsers()
   └─ userService.retrieveAllStudent()
                   │  ↓ UserServiceImpl.retrieveAllStudent()
                   │
                   └─ studentRepository.findAll()
                                        │  ↓ Hibernate generates:
                                        │
                                        │  SELECT * FROM student
                                        │
                                        │  Returns List<Student>
                                        │
                   Loop: for each Student → new StudentResponseDTO(id, firstName, ...)
                   Returns List<StudentResponseDTO>
                   │
   HTTP Response: JSON array
```

---

### READ ONE — GET /user/{id}

```
Request:
  GET http://localhost:8080/user/1

Response:
  { "id": 1, "firstName": "Annie", "lastName": "Raquem", "midtermGrade": 85.0, "finalGrade": 90.0 }
```

**Code path (MyBatis path):**

```
1. UserRestController.getUsersById(@PathVariable Long id)  ← id = 1L
   └─ studentService.getUserById(1L)
                      │  ↓ StudentServiceImpl.getUserById()  (MyBatis)
                      │
                      └─ studentMapper.getUserById(1L)
                                        │  ↓ MyBatis looks up StudentMapper.xml
                                        │    namespace = StudentMapper interface
                                        │    id = "getUserById"
                                        │
                                        │  SELECT * FROM student WHERE id = 1
                                        │
                                        │  MySQL returns row:
                                        │  { id:1, first_name:"Annie", last_name:"Raquem",
                                        │    midterm_grade:85.0, final_grade:90.0 }
                                        │
                                        │  resultMap applies:
                                        │  first_name   → firstName
                                        │  last_name    → lastName
                                        │  midterm_grade → midtermGrade
                                        │  final_grade  → finalGrade
                                        │
                                        │  Returns StudentM object
                      │
                      new StudentResponseDTO(student.getId(), ...)
                      │
   HTTP Response: JSON object
```

---

### SEARCH — GET /user/filter

```
Request:
  GET http://localhost:8080/user/filter?lastName=Raquem&firstName=Annie

Response:
  [{ "id": 1, "firstName": "Annie", "lastName": "Raquem", "midtermGrade": 85.0, "finalGrade": 90.0 }]
```

**Code path:**

```
1. UserRestController.searchByName(
       @RequestParam("lastName") String name = "Raquem",
       @RequestParam String firstName = "Annie"
   )
   │  Objects.nonNull("Raquem") && Objects.nonNull("Annie") → true
   └─ userService.searchByName("Raquem", "Annie")
                   │  ↓ UserServiceImpl.searchByName()
                   │
                   └─ studentRepository.findByFirstNameAndLastName("Annie", "Raquem")
                                        │  ↓ Spring Data derives SQL:
                                        │
                                        │  SELECT * FROM student
                                        │  WHERE first_name = 'Annie'
                                        │    AND last_name  = 'Raquem'
                                        │
                                        │  Returns List<Student>
                                        │
                   .stream().map(student → new StudentResponseDTO(...)).toList()
                   │
   HTTP Response: JSON array (filtered)
```

---

### DELETE — DELETE /user/{id}

```
Request:
  DELETE http://localhost:8080/user/1

Response:
  true   ← student was found and deleted
  false  ← student not found
```

**Code path:**

```
1. UserRestController.deleteStudent(@PathVariable Long id)  ← id = 1L
   └─ userService.deleteStudent(1L)
                   │  ↓ UserServiceImpl.deleteStudent()
                   │
                   ├─ studentRepository.existsById(1L)
                   │       │  ↓ Hibernate generates:
                   │       │  SELECT COUNT(*) FROM student WHERE id = 1
                   │       │  → returns true (found)
                   │
                   └─ studentRepository.deleteById(1L)
                               │  ↓ Hibernate generates:
                               │  DELETE FROM student WHERE id = 1
                               │  MySQL deletes the row
                               │
                   returns true
                   │
   HTTP Response: true
```

---

## 11. Note: Legacy In-Memory Controller

**File:** `src/main/java/com/pup/taguig/app/controller/StudentController.java`

```java
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final List<Student> students = new ArrayList<>();  // ← no DB, in-memory only

    @GetMapping
    public List<Student> getAllStudents() { return students; }

    @PostMapping
    public Student createStudent(@RequestBody StudentRequest request) {
        Student student = new Student(...);
        students.add(student);   // ← saves to List, not database
        return student;
    }

    @GetMapping("/{index}")
    public Student getStudent(@PathVariable int index) { ... }   // uses list index, not DB id

    @DeleteMapping("/{index}")
    public String deleteStudent(@PathVariable int index) { ... }  // removes from list

    public static class StudentRequest { ... }  // inner DTO class, not StudentRequestDTO
}
```

**Status:** Legacy / early version. Uses a local `ArrayList` instead of a database. Data is **lost on app restart**. Currently wired into `UserRestController` via constructor injection but endpoints are separate (`/api/students` not `/user`).

---

## Summary: Who calls what

```
Endpoint                 Controller Method          Service              Repository / Mapper
─────────────────────────────────────────────────────────────────────────────────────────────
POST   /user/            addStudent()               UserServiceImpl      StudentRepository.save()
GET    /user/            getAllUsers()               UserServiceImpl      StudentRepository.findAll()
GET    /user/{id}        getUsersById()             StudentServiceImpl   StudentMapper.getUserById() → XML
GET    /user/filter      searchByName()             UserServiceImpl      StudentRepository.findByFirstNameAndLastName()
DELETE /user/{id}        deleteStudent()            UserServiceImpl      StudentRepository.existsById() + deleteById()
```
