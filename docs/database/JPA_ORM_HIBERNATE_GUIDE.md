# JPA, ORM & Hibernate — Database Connection Guide

> **Project:** appdev (Spring Boot 3.4.4 + MySQL)
> **Covers:** `Student`, `Activity` entities — JPA/Hibernate stack

---

## Table of Contents
1. [What is ORM?](#1-what-is-orm)
2. [JPA vs Hibernate — Ano Difference?](#2-jpa-vs-hibernate--ano-difference)
3. [Dependencies Used](#3-dependencies-used)
4. [Database Setup (application.properties)](#4-database-setup-applicationproperties)
5. [Model Annotations Explained](#5-model-annotations-explained)
6. [JpaRepository — The Magic Interface](#6-jparepository--the-magic-interface)
7. [Built-in Methods](#7-built-in-methods)
8. [Custom Query Methods](#8-custom-query-methods)
9. [Full Request Flow](#9-full-request-flow)

---

## 1. What is ORM?

**ORM = Object-Relational Mapping**

Basically, ORM ang nag-translate ng iyong Java class papunta sa database table — at vice versa. Hindi ka na kailangang mag-sulat ng raw SQL para sa basic CRUD.

```
Java Class (Student.java)         →    Database Table (student)
─────────────────────────────────────────────────────────────
long id                           →    id BIGINT AUTO_INCREMENT
String firstName                  →    first_name VARCHAR(100)
String lastName                   →    last_name VARCHAR(255)
float midtermGrade                →    midterm_grade FLOAT
float finalGrade                  →    final_grade FLOAT
```

Ang `@Entity` annotation ang nagsasabi sa Hibernate: *"Hey, i-map mo ang class na ito sa isang database table."*

---

## 2. JPA vs Hibernate — Ano Difference?

Think of it like an interface and its implementation:

```
JPA (Jakarta Persistence API)    ←  Specification / Interface (contract lang)
        ↓
Hibernate                        ←  Actual implementation (ginagawa ang trabaho)
```

| | JPA | Hibernate |
|---|---|---|
| **Ano siya** | Specification (set of rules) | Framework (nagpapatupad ng rules) |
| **Analogiya** | `UserService` interface | `UserServiceImpl` class |
| **Annotations** | `@Entity`, `@Id`, `@Column` | Reads these annotations and acts on them |

Sa ating project, gumagamit tayo ng JPA annotations sa models — pero si Hibernate ang actual na nag-egenerate ng SQL at nag-kokonekta sa MySQL.

---

## 3. Dependencies Used

**pom.xml:**

```xml
<!-- JPA + Hibernate (bundled together) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- MySQL Driver — para makakonekta sa MySQL database -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

**Bakit dalawa?**
- `spring-boot-starter-data-jpa` — brings in JPA + Hibernate + Spring Data
- `mysql-connector-j` — the actual JDBC driver that lets Java talk to MySQL

---

## 4. Database Setup (application.properties)

```properties
# ─── Database Connection ───────────────────────────────────────────
spring.datasource.url=jdbc:mysql://localhost:3306/appdev_db_try
spring.datasource.username=niera
spring.datasource.password=appdevpass123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# ─── JPA / Hibernate Settings ──────────────────────────────────────
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### Line-by-Line Breakdown

| Property | Value | What it does |
|---|---|---|
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/appdev_db_try` | Connects to MySQL at port 3306, database named `appdev_db_try` |
| `spring.datasource.username` | `niera` | MySQL login username |
| `spring.datasource.password` | `appdevpass123` | MySQL login password |
| `spring.datasource.driver-class-name` | `com.mysql.cj.jdbc.Driver` | Tells Spring which JDBC driver to use |
| `spring.jpa.hibernate.ddl-auto` | `update` | Auto-updates the DB schema based on your entity classes on startup |
| `spring.jpa.show-sql` | `true` | Prints the generated SQL in the console (good for debugging) |
| `spring.jpa.properties.hibernate.format_sql` | `true` | Makes the printed SQL readable/formatted |

### `ddl-auto` Options

| Value | Behavior |
|---|---|
| `create` | Drops and recreates tables every run (data lost!) |
| `create-drop` | Creates on start, drops on shutdown |
| `update` | Adds new columns/tables but never removes (safe for dev) |
| `validate` | Only checks if schema matches, throws error if not |
| `none` | Does nothing — you manage the schema manually |

> Sa ating project: `update` — so kapag nagdagdag ka ng field sa `Student.java`, automatic na mag-aadd ng column sa database.

---

## 5. Model Annotations Explained

### Student.java

```java
@Entity                                        // ← marks this class as a DB table
public class Student {

    @Id                                        // ← this field = primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ← AUTO_INCREMENT in MySQL
    private long id;

    @Column(nullable = false, length = 100, unique = true)
    private String firstName;                  // → first_name VARCHAR(100) NOT NULL UNIQUE

    @Column(nullable = false, insertable = true, updatable = false)
    private String lastName;                   // → last_name VARCHAR(255) NOT NULL (can't be updated)

    @Column(nullable = true)
    private float midtermGrade;               // → midterm_grade FLOAT (optional)

    @Column(nullable = true)
    private float finalGrade;                 // → final_grade FLOAT (optional)
}
```

### Activity.java

```java
@Entity
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;                      // → name VARCHAR(200) NOT NULL

    @Column(nullable = true)
    private String description;              // → description TEXT (optional)

    @Column(nullable = false)
    private LocalDateTime createdAt;         // → created_at DATETIME NOT NULL
}
```

### Annotation Reference

| Annotation | Purpose |
|---|---|
| `@Entity` | Declares this class as a JPA entity (maps to a table) |
| `@Id` | Marks the primary key field |
| `@GeneratedValue(strategy = GenerationType.IDENTITY)` | Let the DB auto-generate the ID (AUTO_INCREMENT) |
| `@Column(nullable = false)` | Makes the column NOT NULL in the DB |
| `@Column(length = 100)` | Sets VARCHAR length |
| `@Column(unique = true)` | Adds UNIQUE constraint |
| `@Column(updatable = false)` | Field can be set on insert but never changed after |

---

## 6. JpaRepository — The Magic Interface

```java
// StudentRepository.java
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByFirstNameAndLastName(String firstName, String name);
}

// ActivityRepository.java
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
}

// UserRepository.java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
```

### What does `extends JpaRepository<Student, Long>` mean?

```
JpaRepository< Student , Long >
               ───────   ────
               Entity    Type ng Primary Key (@Id field)
               Class
```

- First param = ang Entity class na naka-map sa database table
- Second param = ang type ng `@Id` field

Pagka-extend mo ng `JpaRepository`, libre na lahat ng CRUD methods — di mo na kailangang i-implement pa.

---

## 7. Built-in Methods

Lahat ng ito ay libre — si Spring Data JPA ang nag-iimplementa para sa iyo:

| Method | SQL Equivalent | Used In |
|---|---|---|
| `save(entity)` | `INSERT` or `UPDATE` | `UserServiceImpl.addUser()`, `ActivityServiceImpl.createActivity()` |
| `findAll()` | `SELECT * FROM table` | `UserServiceImpl.retrieveAllStudent()`, `ActivityServiceImpl.getAllActivities()` |
| `findById(id)` | `SELECT * FROM table WHERE id = ?` | `UserServiceImpl.getUserById()`, `ActivityServiceImpl.getActivityById()` |
| `existsById(id)` | `SELECT COUNT(*) WHERE id = ?` | `UserServiceImpl.deleteStudent()`, `ActivityServiceImpl.deleteActivity()` |
| `deleteById(id)` | `DELETE FROM table WHERE id = ?` | `UserServiceImpl.deleteStudent()`, `ActivityServiceImpl.deleteActivity()` |

### Example from UserServiceImpl.java

```java
// save() — INSERT new student, returns the saved entity with auto-generated ID
Student student = new Student(request.getFirstName(), request.getLastName(), ...);
student = studentRepository.save(student);    // ← Hibernate runs INSERT, sets the ID
return student.getId();

// findById() — returns Optional (might not exist)
Student student = studentRepository.findById(id)
    .orElseThrow(() -> new RuntimeException("Student not found"));

// existsById() + deleteById() — safe delete pattern
if (studentRepository.existsById(id)) {
    studentRepository.deleteById(id);
    return true;
}
return false;
```

---

## 8. Custom Query Methods

```java
// StudentRepository.java
List<Student> findByFirstNameAndLastName(String firstName, String name);
```

**Paano ito gumagana?** Spring Data JPA reads the method name and auto-generates the SQL:

```
findBy  FirstName  And  LastName
  ↓         ↓      ↓       ↓
SELECT   WHERE first_name = ?  AND  last_name = ?
```

Ginagamit sa `UserServiceImpl.searchByName()`:

```java
List<Student> students = studentRepository.findByFirstNameAndLastName(firstName, name);
```

### Naming Convention for Derived Queries

| Method Name | Generated SQL |
|---|---|
| `findByFirstName(String name)` | `WHERE first_name = ?` |
| `findByFirstNameAndLastName(...)` | `WHERE first_name = ? AND last_name = ?` |
| `findByMidtermGradeGreaterThan(float g)` | `WHERE midterm_grade > ?` |
| `findByLastNameContaining(String s)` | `WHERE last_name LIKE '%s%'` |

---

## 9. Full Request Flow

### Example: `POST /user/` (Add Student)

```
HTTP POST /user/
Body: { "firstName": "Annie", "lastName": "Raquem", "midtermGrade": 85, "finalGrade": 90 }

1. UserRestController.addStudent()
   └─ receives StudentRequestDTO from @RequestBody

2. UserServiceImpl.addUser(request)
   └─ creates new Student(firstName, lastName, midtermGrade, finalGrade)

3. studentRepository.save(student)
   └─ Hibernate generates SQL:
      INSERT INTO student (first_name, last_name, midterm_grade, final_grade)
      VALUES ('Annie', 'Raquem', 85.0, 90.0)
   └─ MySQL assigns id = 1 (AUTO_INCREMENT)

4. student.getId() → returns 1L

5. UserRestController returns 1L to client
```

### Example: `GET /user/1` (Get by ID)

```
HTTP GET /user/1

1. UserRestController.getUsersById(1L)

2. StudentServiceImpl.getUserById(1L)      ← Note: MyBatis StudentServiceImpl
   └─ studentMapper.getUserById(1L)        ← hits StudentMapper.xml SQL

   [OR for JPA path, UserServiceImpl.getUserById(1L):]
   └─ studentRepository.findById(1L)
      └─ Hibernate: SELECT * FROM student WHERE id = 1
      └─ Maps result → Student object

3. Returns StudentResponseDTO { id, firstName, lastName, midtermGrade, finalGrade }
```

### Architecture Summary

```
HTTP Request
    ↓
UserRestController / ActivityController
    ↓
UserServiceImpl / ActivityServiceImpl
    ↓
StudentRepository / ActivityRepository
(extends JpaRepository)
    ↓
Hibernate (ORM engine — translates Java ↔ SQL)
    ↓
MySQL (appdev_db_try)
```
