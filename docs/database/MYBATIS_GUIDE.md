# MyBatis — Database Access Guide

> **Project:** appdev (Spring Boot 3.4.4 + MySQL)
> **Covers:** `StudentM` entity — MyBatis stack (used in StudentMapper / StudentServiceImpl)

---

## Table of Contents
1. [What is MyBatis?](#1-what-is-mybatis)
2. [MyBatis vs JPA — Key Difference](#2-mybatis-vs-jpa--key-difference)
3. [Dependencies Used](#3-dependencies-used)
4. [Database Setup (application.properties)](#4-database-setup-applicationproperties)
5. [The Model — StudentM.java](#5-the-model--studentmjava)
6. [The Mapper Interface — StudentMapper.java](#6-the-mapper-interface--studentmapperjava)
7. [The XML Mapper — StudentMapper.xml](#7-the-xml-mapper--studentmapperxml)
8. [The Service Layer — StudentServiceImpl.java](#8-the-service-layer--studentserviceimpljava)
9. [Full Request Flow](#9-full-request-flow)
10. [When to Use MyBatis vs JPA](#10-when-to-use-mybatis-vs-jpa)

---

## 1. What is MyBatis?

MyBatis is a **SQL Mapper framework** — ibig sabihin, **ikaw mismo ang nagsusulat ng SQL**, at si MyBatis ang nag-aasikaso ng pagkonekta ng iyong SQL results papunta sa Java objects.

Iba ito sa JPA/Hibernate na auto-generate ang SQL based sa annotations at method names.

```
JPA/Hibernate:  Student → @Entity → Hibernate writes SQL for you
MyBatis:        Student → you write SQL → MyBatis maps the result back
```

---

## 2. MyBatis vs JPA — Key Difference

| | JPA / Hibernate | MyBatis |
|---|---|---|
| **SQL** | Auto-generated | You write it yourself |
| **Model annotations** | `@Entity`, `@Id`, `@Column` required | Plain Java class (no annotations needed) |
| **Repository** | Extends `JpaRepository` | Interface with `@Mapper` annotation |
| **Mapping config** | Annotations on fields | `resultMap` in XML file |
| **Complexity** | Simple CRUD = easy | Simple CRUD = more code, but total control over SQL |
| **Best for** | Standard CRUD operations | Complex queries, stored procedures, fine-tuned SQL |
| **Our project uses it for** | `Student`, `Activity`, `User` entities | `StudentM` entity (read-only: getById, getAll) |

---

## 3. Dependencies Used

**pom.xml:**

```xml
<!-- MyBatis Spring Boot Starter -->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>3.0.3</version>
</dependency>

<!-- MySQL Driver (shared with JPA) -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

**`mybatis-spring-boot-starter`** bundles:
- MyBatis core
- MyBatis-Spring integration
- Auto-configuration for Spring Boot (auto-detects `@Mapper` interfaces)

---

## 4. Database Setup (application.properties)

```properties
# ─── Same MySQL connection as JPA ──────────────────────────────────
spring.datasource.url=jdbc:mysql://localhost:3306/appdev_db_try
spring.datasource.username=niera
spring.datasource.password=appdevpass123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# ─── MyBatis-specific config ───────────────────────────────────────
mybatis.mapper-locations=classpath:mappers/*.xml
mybatis.type-aliases-package=com.pup.taguig.app.model
```

### MyBatis Config Explained

| Property | Value | What it does |
|---|---|---|
| `mybatis.mapper-locations` | `classpath:mappers/*.xml` | Tells MyBatis where to find your SQL XML files. `classpath:` = `src/main/resources/`. So it looks in `src/main/resources/mappers/` |
| `mybatis.type-aliases-package` | `com.pup.taguig.app.model` | Registers your model classes as type aliases — so you can write `StudentM` in XML instead of the full `com.pup.taguig.app.model.StudentM` |

**File location of the XML:**
```
src/
└── main/
    └── resources/
        └── mappers/
            └── StudentMapper.xml    ← this is what mybatis.mapper-locations points to
```

---

## 5. The Model — StudentM.java

```java
// model/StudentM.java
public class StudentM {

    private long id;
    private String firstName;
    private String lastName;
    private float midtermGrade;
    private float finalGrade;

    // constructors, getters, setters...
}
```

**Key difference from `Student.java` (JPA version):**
- **Walang `@Entity`, `@Id`, `@Column` annotations**
- Ito ay isang plain Java class (POJO — Plain Old Java Object)
- Si MyBatis mismo ang mag-ha-handle ng mapping via the XML `resultMap`

---

## 6. The Mapper Interface — StudentMapper.java

```java
// repositoryM/StudentMapper.java
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

### What is `@Mapper`?

`@Mapper` is the MyBatis equivalent of `@Repository`. It tells MyBatis (and Spring Boot):
- *"I-scan mo ito as a MyBatis mapper interface"*
- *"Auto-implement mo ito at i-inject bilang a Spring bean"*

### How MyBatis connects the interface to the XML

The method names in `StudentMapper.java` **must exactly match** the `id` attribute in the XML queries:

```
Java interface method:      StudentMapper.getUserById(Long id)
                                          ↕ must match
XML query id:               <select id="getUserById" ...>
```

---

## 7. The XML Mapper — StudentMapper.xml

**Location:** `src/main/resources/mappers/StudentMapper.xml`

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.pup.taguig.app.repositoryM.StudentMapper">

    <!-- resultMap: defines how DB columns map to Java fields -->
    <resultMap id="studentResultMap" type="com.pup.taguig.app.model.StudentM">
        <id     column="id"             property="id"/>
        <result column="first_name"     property="firstName"/>
        <result column="last_name"      property="lastName"/>
        <result column="midterm_grade"  property="midtermGrade"/>
        <result column="final_grade"    property="finalGrade"/>
    </resultMap>

    <!-- SELECT by ID -->
    <select id="getUserById" resultMap="studentResultMap">
        SELECT * FROM student WHERE id = #{id}
    </select>

</mapper>
```

### Breaking It Down

#### `namespace`
```xml
<mapper namespace="com.pup.taguig.app.repositoryM.StudentMapper">
```
Ito ang nagko-connect ng XML file sa Java interface. Ang full class path ng `StudentMapper` ang value — dapat exact match.

#### `resultMap`
```xml
<resultMap id="studentResultMap" type="com.pup.taguig.app.model.StudentM">
    <id     column="id"             property="id"/>
    <result column="first_name"     property="firstName"/>
    <result column="last_name"      property="lastName"/>
    <result column="midterm_grade"  property="midtermGrade"/>
    <result column="final_grade"    property="finalGrade"/>
</resultMap>
```

Ito ang pinaka-importanteng bahagi ng MyBatis setup.

| Attribute | Meaning |
|---|---|
| `id` | Name of this resultMap (referenced by queries) |
| `type` | The Java class to map results into (`StudentM`) |
| `column` | The **database column name** (snake_case — same as your MySQL table) |
| `property` | The **Java field name** (camelCase — same as `StudentM.java`) |
| `<id>` tag | Marks the primary key column |
| `<result>` tag | Maps a regular column |

**Why is `resultMap` needed?**

```
MySQL column:     first_name    (snake_case)
Java field:       firstName     (camelCase)
```

Java and MySQL have different naming conventions. Si `resultMap` ang nag-translate sa pagitan nila. Kung pareho ang spelling (e.g., `id` = `id`), hindi na kailangan ng explicit mapping — pero mas malinaw kung i-declare mo pa rin.

#### The Query
```xml
<select id="getUserById" resultMap="studentResultMap">
    SELECT * FROM student WHERE id = #{id}
</select>
```

| Part | Meaning |
|---|---|
| `id="getUserById"` | Matches the method name in `StudentMapper.java` |
| `resultMap="studentResultMap"` | Use the resultMap defined above to map results |
| `SELECT * FROM student WHERE id = #{id}` | Your actual SQL — you write this yourself |
| `#{id}` | MyBatis parameter placeholder (equivalent to `?` in JDBC, but named). Maps to the `Long id` parameter in the Java method |

**Note:** `#{id}` is safe from SQL injection — MyBatis treats it as a prepared statement parameter.

---

## 8. The Service Layer — StudentServiceImpl.java

```java
// service/impl/StudentServiceImpl.java
@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;    // ← MyBatis injects this automatically

    @Override
    public StudentResponseDTO getUserById(Long id) {
        StudentM student = studentMapper.getUserById(id);    // ← calls XML SQL query

        return new StudentResponseDTO(
            student.getId(),
            student.getFirstName(),
            student.getLastName(),
            student.getMidtermGrade(),
            student.getFinalGrade()
        );
    }

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

**Note:** `StudentServiceImpl` (MyBatis) implements `StudentService` — which is used by `UserRestController` for the `GET /user/{id}` endpoint.

---

## 9. Full Request Flow

### `GET /user/{id}` — Get Student by ID (MyBatis path)

```
HTTP GET /user/1

1. UserRestController.getUsersById(1L)
   └─ calls studentService.getUserById(1L)
      (studentService is StudentServiceImpl — MyBatis version)

2. StudentServiceImpl.getUserById(1L)
   └─ calls studentMapper.getUserById(1L)

3. MyBatis looks up StudentMapper interface → finds matching XML:
   namespace="com.pup.taguig.app.repositoryM.StudentMapper"
   id="getUserById"

4. Executes SQL:
   SELECT * FROM student WHERE id = 1

5. MySQL returns row:
   { id: 1, first_name: "Annie", last_name: "Raquem", midterm_grade: 85.0, final_grade: 90.0 }

6. MyBatis applies resultMap (studentResultMap):
   first_name  →  firstName
   last_name   →  lastName
   midterm_grade → midtermGrade
   final_grade   → finalGrade
   → Creates StudentM object

7. StudentServiceImpl converts StudentM → StudentResponseDTO

8. Returns JSON:
   { "id": 1, "firstName": "Annie", "lastName": "Raquem", "midtermGrade": 85.0, "finalGrade": 90.0 }
```

### Architecture Summary

```
HTTP Request (GET /user/1)
    ↓
UserRestController
    ↓
StudentServiceImpl   (implements StudentService)
    ↓
StudentMapper        (@Mapper interface — repositoryM package)
    ↓
StudentMapper.xml    (your SQL lives here — src/main/resources/mappers/)
    ↓
MySQL (appdev_db_try → student table)
    ↓
resultMap            (maps snake_case columns → camelCase Java fields)
    ↓
StudentM object
    ↓
StudentResponseDTO   (returned to controller)
```

---

## 10. When to Use MyBatis vs JPA

| Use Case | Prefer |
|---|---|
| Simple CRUD (save, findAll, findById, delete) | JPA — less code, auto-generated |
| Complex JOINs across multiple tables | MyBatis — write the exact SQL |
| Stored procedures | MyBatis — direct SQL support |
| Need full control over the SQL query | MyBatis |
| Rapid development, standard operations | JPA |
| Performance-critical queries | MyBatis — no ORM overhead |

**Sa ating project:**
- `Student` (JPA) → full CRUD via `StudentRepository extends JpaRepository`
- `StudentM` (MyBatis) → read-only queries (`getUserById`, `retrieveAllStudent`) via `StudentMapper.xml`

Both connect to the same MySQL database (`appdev_db_try`) and the same `student` table.
