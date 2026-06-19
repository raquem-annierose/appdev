# EXAM PRACTICE: Spring Boot + MyBatis (Based on Actual Project Code)

Scope: lahat ng layer na direktang konektado sa Student ↔ Department MyBatis flow — Mapper Interface, Model, DTO (Response/Request hiwalay), MyBatis XML, Service Interface, Service Impl (Department/Student hiwalay), Controller. 55 blanks total.

Format: ang sagot nakalagay na agad sa loob ng blank tag, hal. `@[BLANK_12] (answer: Autowired)`. Reason/explanation nasa **ibaba mismo ng bawat layer**, hindi na sa dulo ng buong file.

---

## SECTION 1: REPOSITORY / MAPPER INTERFACE LAYER

```java
package com.pup.taguig.app.repositoryM;

@[BLANK_1] (answer: Mapper)
public interface StudentMapper {

    public StudentM getUserById([BLANK_2] (answer: Long) id);
    public List<StudentM> retrieveAllStudent();
    public [BLANK_3] (answer: Long) addUser(StudentM request);
    public Long insertUser(StudentM student);
    public Long insertStudent(StudentM student);
    public int deleteStudentById(Long id);
}
```

```java
package com.pup.taguig.app.repository;

@[BLANK_4] (answer: Mapper)
public interface DepartmentMapper {

    public [BLANK_5] (answer: Department) getDepartmentAndStudentsById(Long id);
}
```

**Reason per blank — Section 1:**
1. `@Mapper` — kailangan ito para ma-scan ng Spring/MyBatis ang interface bilang mapper proxy; kung wala nito, walang concrete implementation na maibibigay sa `@Autowired` sa service.
2. `Long` — `id` ay primary key ng student, at `Long` ang consistent type na ginagamit sa buong project para dito (`#{id}`, `keyProperty="id"`).
3. `Long` — kahit unused method, dapat consistent ang return type sa pattern ng ibang insert methods (`insertUser`, `insertStudent`) na lahat `Long`.
4. `@Mapper` — same reason sa #1, para sa `DepartmentMapper`.
5. `Department` — ang return type ay dapat tugma sa `type="...model.Department"` na nasa `resultMap` ng XML, dahil ito ang object na binubuo at ibinabalik ng query.

---

## SECTION 2: MODEL LAYER (bahaging direktang ginagamit ng `<collection>` sa XML)

```java
package com.pup.taguig.app.model;

@[BLANK_6] (answer: Getter)
@[BLANK_7] (answer: Setter)
public class Department {
    private int id;
    private String name;
    private String displayName;

    private [BLANK_8] (answer: List)<StudentM> students;
}
```

**Reason per blank — Section 2:**
6. `@Getter` — walang manual getter methods sa file, kaya Lombok ang gumagawa nito automatically; kailangan ito para magamit ng MyBatis ang reflection-based access sa fields.
7. `@Setter` — kailangan ito kasi MyBatis ang gagawa ng `Department` object at gagamit ng setter (`setStudents`, `setName`, etc.) para i-populate ang fields mula sa query result.
8. `List` — "isang department, marami students" (one-to-many), kaya dapat collection type ang property para tumugma sa `<collection>` block sa XML — kung hindi `List`, walang malalagyan ng multiple rows na resulta ng JOIN.

---

## SECTION 3: DTO LAYER — RESPONSE DTO

```java
package com.pup.taguig.app.dto;

public class StudentResponseDTO {
     private long id;
     private String firstName;
     private String lastName;
     private float midtermGrade;
     private float finalGrade;

     private [BLANK_9] (answer: DepartmentResponseDTO) department;

     public StudentResponseDTO() {}

     public StudentResponseDTO([BLANK_10] (answer: Long) id, String firstName, String lastName, float midtermGrade, float finalGrade) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.midtermGrade = midtermGrade;
        this.finalGrade = finalGrade;
     }
}
```

```java
package com.pup.taguig.app.dto;

@Getter
@Setter
@[BLANK_11] (answer: NoArgsConstructor)
public class DepartmentResponseDTO {
    private int id;
    private String name;
    private String displayName;

    private List<[BLANK_12] (answer: StudentResponseDTO)> students;
}
```

**Reason per blank — Section 3 (Response DTO):**
9. `DepartmentResponseDTO` — hindi dapat raw model (`Department`) ang nested field sa loob ng isang Response DTO; layered architecture rule: DTO-to-DTO lang papuntang labas ng controller, hindi entity/model.
10. `Long` — ang constructor ay tinatawag gamit `student.getId()` na nagbabalik ng `Long`, kaya dapat tugma ang parameter type.
11. `NoArgsConstructor` — kasi ginagawa itong `new DepartmentResponseDTO()` na walang args sa `DepartmentServiceImpl`, kaya kailangan may no-arg constructor.
12. `StudentResponseDTO` — isang Department response ay "holder" ng listahan ng student responses, hindi raw student model — kaya `List<StudentResponseDTO>`, para consistent ang buong response tree sa DTO layer.

---

## SECTION 4: DTO LAYER — REQUEST DTO

```java
package com.pup.taguig.app.dto;

public class StudentRequestDTO {
     private [BLANK_13] (answer: long) id;
     private String firstName;
     private String lastName;
     private [BLANK_14] (answer: float) midtermGrade;
     private float finalGrade;
}
```

```java
package com.pup.taguig.app.dto;

@Getter
@Setter
public class DepartmentRequestDTO {
    private [BLANK_15] (answer: int) id;
    private String name;
    private String [BLANK_16] (answer: displayName);
}
```

**Reason per blank — Section 4 (Request DTO):**
13. `long` — dapat tugma ang `StudentRequestDTO.id` sa `Student`/`StudentM` model (`long id`), kasi ito ang papasok na request bago pa man ma-insert sa DB.
14. `float` — `midtermGrade` at `finalGrade` ay grado, kaya `float` type, tugma sa model at sa column type sa DB.
15. `int` — sadyang `int` ang `Department.id` (hindi `long`/`Long` gaya ng Student) — kapansin-pansin na inconsistency sa project, pero dapat tumutugma ang DTO sa model na ito kinukuha.
16. `displayName` — tugma dapat sa `Department` model field na ito ang gagamitin pag binuo ang request object papunta sa service/mapper.

---

## SECTION 5: MYBATIS XML LAYER — `StudentMapper.xml`

```xml
<mapper [BLANK_17] (answer: namespace)="com.pup.taguig.app.repositoryM.StudentMapper">

    <resultMap id="studentResultMap" type="com.pup.taguig.app.model.[BLANK_18] (answer: StudentM)">
        <[BLANK_19] (answer: id) column="stud_id" property="id"/>
        <result column="first_name" property="firstName"/>
        <result column="last_name" property="lastName"/>
        <result column="midterm_grade" property="midtermGrade"/>
        <result column="final_grade" property="finalGrade"/>

        <[BLANK_20] (answer: association) property="department" resultMap="[BLANK_21] (answer: departmentResultMap)"/>
    </resultMap>

    <resultMap id="departmentResultMap" type="com.pup.taguig.app.model.Department">
        <id column="dept_id" property="id"/>
        <result column="name" property="name"/>
        <result column="display_name" property="displayName"/>
    </resultMap>

    <select id="getUserById" resultMap="[BLANK_22] (answer: studentResultMap)">
        SELECT
            s.id AS stud_id,
            s.first_name,
            s.last_name,
            s.midterm_grade,
            s.final_grade,
            d.id AS dept_id,
            d.name,
            d.display_name
        FROM
            student s
        [BLANK_23] (answer: LEFT) JOIN departments d
            ON s.department_id = d.id
        WHERE
            s.id = [BLANK_24] (answer: #){id}
    </select>

    <select id="retrieveAllStudent" resultMap="studentResultMap">
        SELECT
            s.id AS stud_id, s.first_name, s.last_name, s.midterm_grade, s.final_grade,
            d.id AS dept_id, d.name, d.display_name
        FROM
            student s
        LEFT JOIN departments d
            ON s.department_id = d.[BLANK_25] (answer: id)
    </select>

    <insert id="insertStudent" [BLANK_26] (answer: useGeneratedKeys)="true" keyProperty="[BLANK_27] (answer: id)">
        INSERT INTO student
            (first_name, last_name, midterm_grade, final_grade)
        VALUES
            (#{firstName}, #{lastName}, #{midtermGrade}, #{finalGrade})
    </insert>

    <[BLANK_28] (answer: update) id="updateStudent">
        UPDATE student
        SET
            first_name = #{firstName},
            last_name = #{lastName},
            midterm_grade = #{midtermGrade},
            final_grade = #{[BLANK_29] (answer: finalGrade)}
        WHERE
            id = #{id}
    </[BLANK_28]>

    <[BLANK_30] (answer: delete) id="deleteStudentById" parameterType="[BLANK_31] (answer: Long)">
        DELETE FROM student
        WHERE
            id = #{id}
    </[BLANK_30]>
</mapper>
```

**Reason per blank — Section 5 (StudentMapper.xml):**
17. `namespace` — ito ang nag-uugnay ng buong XML file sa specific Java mapper interface; kung mali, hindi mahahanap ni MyBatis ang method binding (`BindingException`).
18. `StudentM` — dapat tugma ang `type` attribute sa aktwal na model class na pinopulate ng resultMap (`StudentM`, hindi `Student` — dalawa silang magkaibang class sa project).
19. `id` — required ang `<id>` tag para malaman ng MyBatis kung anong column ang primary key; importante ito pag may JOIN para malaman kung "same row/object" ba ito.
20. `association` — "isang student, isang department" (many-to-one), kaya `association` — para mabuo ang nested `Department` object sa loob ng `StudentM`.
21. `departmentResultMap` — reuse ng existing resultMap sa halip na ulitin ang mapping ng dept_id/name/display_name — isang lugar lang ang pinagmumulan ng truth.
22. `studentResultMap` — dapat ang resultMap na may `<association>` ang gamit dahil ang method na ito ay nagbabalik ng `StudentM` na may nested department.
23. `LEFT` — para hindi mawala ang student row kung walang matching department; kung `INNER JOIN`, mawawala ang students na walang department.
24. `#` — safe PreparedStatement placeholder, laban sa SQL injection; hindi gagamitin ang `${}` na raw substitution.
25. `id` — kumpletuhin ang join condition (`d.id`) para tama ang pagtukoy ng department row na dapat i-attach.
26. `useGeneratedKeys` — sinasabi sa MyBatis na kunin ang auto-generated primary key mula sa DB pagkatapos ng INSERT.
27. `id` — ito ang specific property kung saan ilalagay ng MyBatis ang nakuhang generated key.
28. `update` — dapat tugma ang tag name sa aktwal na SQL operation (UPDATE statement), at parehas dapat ang opening/closing tag.
29. `finalGrade` — `#{}` placeholders ay tumutukoy sa Java property names (camelCase), hindi sa DB column names (snake_case) — kaya `finalGrade`, hindi `final_grade`.
30. `delete` — tugma sa DELETE SQL operation; parehas dapat opening/closing tag.
31. `Long` — `parameterType` ay dapat tugma sa method signature (`deleteStudentById(Long id)`), para malaman ng MyBatis kung anong type ang papasok na parameter.

---

## SECTION 6: MYBATIS XML LAYER — `DepartmentMapper.xml`

```xml
<mapper namespace="com.pup.taguig.app.repository.DepartmentMapper">

    <resultMap id="departmentResultMap" type="com.pup.taguig.app.model.Department">
        <id column="dept_id" property="id"/>
        <result column="name" property="name"/>
        <result column="display_name" property="displayName"/>

        <[BLANK_32] (answer: collection) property='students' [BLANK_33] (answer: ofType)="com.pup.taguig.app.model.StudentM">
            <id column="stud_id" property="id"/>
            <result column="first_name" property="firstName"/>
            <result column="last_name" property="lastName"/>
            <result column="midterm_grade" property="midtermGrade"/>
            <result column="final_grade" property="finalGrade"/>
        </[BLANK_32]>

    </resultMap>

    <select id="getDepartmentAndStudentsById" resultMap="[BLANK_34] (answer: departmentResultMap)">
        SELECT
            d.id AS dept_id, d.name, d.display_name,
            s.id AS stud_id, s.first_name, s.last_name
        FROM departments [BLANK_35] (answer: as) d
        [BLANK_36] (answer: JOIN) student as s
            ON s.department_id = d.id
        WHERE d.id = #{[BLANK_37] (answer: id)}
    </select>

</mapper>
```

**Reason per blank — Section 6 (DepartmentMapper.xml):**
32. `collection` — "isang department, marami students" (one-to-many), kabaligtaran ng `association` — para mabuo ang `List<StudentM>` mula sa multiple JOIN rows.
33. `ofType` — sa `<collection>`, ginagamit ang `ofType` (hindi `javaType`) para tukuyin ang klase ng **elemento** ng list, dahil collection na mismo ang property.
34. `departmentResultMap` — dapat ang resultMap na may `<collection>` ang gamit dahil ang query ay nagbabalik ng isang `Department` na may listahan ng students.
35. `as` — table alias lang para mas maikli ang reference (`d.id`, `d.name`).
36. `JOIN` — plain inner join (walang `LEFT`) dahil ang query na ito ay para lang humanap ng department na talagang may students.
37. `id` — ang method parameter ay `Long id`, kaya ito ang gamit sa `#{}` placeholder sa WHERE clause.

---

## SECTION 7: SERVICE INTERFACE LAYER

```java
package com.pup.taguig.app.service;

public interface DepartmentService {
    public [BLANK_38] (answer: DepartmentResponseDTO) getDepartmentAndStudentsById(Long id);
}
```

**Reason per blank — Section 7:**
38. `DepartmentResponseDTO` — ang interface ay dapat magdeklara ng parehong return type na aktwal na ibinabalik ng implementation (`DepartmentServiceImpl`), kung hindi tugma, compile error.

---

## SECTION 8: SERVICE IMPLEMENTATION LAYER — `DepartmentServiceImpl`

```java
package com.pup.taguig.app.service.impl;

@[BLANK_39] (answer: Service)
public class DepartmentServiceImpl implements DepartmentService {

    private static final Logger logger = [BLANK_40] (answer: Logger).getLogger(DepartmentServiceImpl.class.getName());

    @[BLANK_41] (answer: Autowired)
    private DepartmentMapper deptRepository;

    @Override
    public DepartmentResponseDTO getDepartmentAndStudentsById(Long id) {
        DepartmentResponseDTO result = new DepartmentResponseDTO();

        try {
            Department dept = deptRepository.getDepartmentAndStudentsById(id);
            result.setId(dept.getId());
            result.setName(dept.getName());
            result.setDisplayName(dept.getDisplayName());
            if([BLANK_42] (answer: Objects).nonNull(dept.getStudents())) {
                List<StudentResponseDTO> studDtoList = new ArrayList<>();
                for(StudentM student : dept.getStudents()) {
                    StudentResponseDTO students = new StudentResponseDTO();
                    students.setId(student.getId());
                    students.setFirstName(student.getFirstName());
                    students.setLastName(student.getLastName());

                    studDtoList.add(students);
                }
                result.[BLANK_43] (answer: setStudents)(studDtoList);
            }

        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error retrieving department and students by id", e);
        }
        return result;
   }
}
```

**Reason per blank — Section 8 (DepartmentServiceImpl):**
39. `Service` — kailangan para ma-register ang class bilang Spring-managed bean, para magamit sa `@Autowired` injection sa controller.
40. `Logger` — `java.util.logging.Logger.getLogger(...)` ay static factory method sa `Logger` class mismo.
41. `Autowired` — automatic mag-inject ng Spring ang `DepartmentMapper` proxy bean papunta sa `deptRepository`, hindi mo na need manual instantiate.
42. `Objects` — null-safety check bago mag-loop sa `dept.getStudents()`, kasi posibleng walang students ang isang department.
43. `setStudents` — huling step para ilagay ang nabuong listahan papunta sa response object, para makita sa final JSON output.

---

## SECTION 9: SERVICE IMPLEMENTATION LAYER — `StudentServiceImpl`

```java
package com.pup.taguig.app.service.impl;

@Service
public class StudentServiceImpl implements StudentService {

    private static final Logger LOGGER = LogManager.[BLANK_44] (answer: getLogger)(StudentServiceImpl.class);

    @Autowired
    private StudentMapper studentMapper;

    private StudentResponseDTO toDTO(StudentM student) {
        StudentResponseDTO responseDTO = new StudentResponseDTO(
                 student.getId(), student.getFirstName(), student.getLastName(),
                 student.getMidtermGrade(), student.getFinalGrade()
         );

        if(student.getDepartment() != [BLANK_45] (answer: null)) {
            DepartmentResponseDTO deptDTO = new DepartmentResponseDTO();
            deptDTO.setId(student.getDepartment().getId());
            deptDTO.setName(student.getDepartment().getName());
            deptDTO.setDisplayName(student.getDepartment().getDisplayName());
            responseDTO.setDepartment(deptDTO);
        }
        return responseDTO;
    }

    @Override
    public List<StudentResponseDTO> retrieveAllStudent() {
        List<StudentM> students = studentMapper.retrieveAllStudent();
        return students.stream()
                .map(this::toDTO)
                .[BLANK_46] (answer: toList)();
    }

    @Override
    public Long insertStudent(StudentRequestDTO request) {
        StudentM student = new StudentM();
        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setMidtermGrade(request.getMidtermGrade());
        student.setFinalGrade(request.getFinalGrade());

        Long id = studentMapper.insertStudent(student);
        return student.[BLANK_47] (answer: getId)();
    }

    @Override
    public boolean deleteStudentById(Long id) {
        boolean result = false;
        try {
            if (studentMapper.deleteStudentById(id) [BLANK_48] (answer: >) 0) {
                result = true;
            }
        } catch (Exception e) {
            System.out.println("Error deleting student with id " + id + ": " + e.getMessage());
        }
        return result;
    }
}
```

**Reason per blank — Section 9 (StudentServiceImpl):**
44. `getLogger` — sa Log4j2 (`LogManager`), ito ang static method para makakuha ng logger instance na naka-bind sa specific class.
45. `null` — kailangan i-check kung may department talaga ang student bago mag-access ng `.getId()`, etc. — kung wala, `NullPointerException`.
46. `toList` — pagkatapos i-`map` ang bawat `StudentM` papuntang DTO, kailangan i-collect ulit pabalik sa `List`.
47. `getId` — pinopulate na ni MyBatis ang `id` field ng parehong `student` object pagkatapos ng `insertStudent` (via `useGeneratedKeys` + `keyProperty="id"`), kaya may value na agad available.
48. `>` — standard pattern para malaman kung may na-delete talaga: kung mas malaki sa 0 ang affected row count, successful ang operation.

---

## SECTION 10: CONTROLLER LAYER

```java
package com.pup.taguig.app.controller;

@RestController
@[BLANK_49] (answer: RequestMapping)("department")
public class DepartmentController {

    @Autowired
    private DepartmentService deptService;

    @[BLANK_50] (answer: GetMapping)("/{id}")
    public DepartmentResponseDTO getDepartmentAndStudentsById(@[BLANK_51] (answer: PathVariable) Long id) {
        if (Objects.nonNull(id)) {
            return deptService.getDepartmentAndStudentsById(id);
        }
        return null;
    }
}
```

```java
package com.pup.taguig.app.controller;

@RestController
@RequestMapping("user")
public class UserRestController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/{id}")
    public StudentResponseDTO getUsersById(@[BLANK_52] (answer: PathVariable) Long id) {
        return studentService.getUserById(id);
    }

    @[BLANK_53] (answer: PostMapping)("/")
    public Long addStudent(@[BLANK_54] (answer: RequestBody) StudentRequestDTO student) {
        Long result = null;
        if (Objects.nonNull(student)) {
            result = studentService.insertStudent(student);
        }
        return result;
    }

    @[BLANK_55] (answer: DeleteMapping)("/{id}")
    public boolean deleteStudent(@PathVariable Long id) {
        return studentService.deleteStudentById(id);
    }
}
```

**Reason per blank — Section 10:**
49. `RequestMapping` — itinatakda ang base path (`"department"`) para sa lahat ng endpoints sa controller na ito.
50. `GetMapping` — read-only lookup by id → dapat HTTP GET na operation.
51. `PathVariable` — kinukuha ang `id` mula sa URL path segment (`/{id}`), hindi galing sa query string o body.
52. `PathVariable` — same reasoning sa #51, ibang controller lang.
53. `PostMapping` — paggawa ng bagong student record → dapat HTTP POST na operation.
54. `RequestBody` — kailangan ito para i-parse ang incoming JSON payload papunta sa `StudentRequestDTO` object.
55. `DeleteMapping` — pagtanggal ng student by id → dapat HTTP DELETE na operation.

---

## Overall Watchpoints

- **`association` vs `collection`**: `association` = isang nested object (many-to-one, `StudentMapper.xml`); `collection` = listahan (one-to-many, `DepartmentMapper.xml`). Magkaiba ang type attribute: `resultMap`/`javaType` para sa association, `ofType` para sa collection.
- **`#{}` vs `${}`**: `#{}` lang ginagamit sa buong project — SQL-injection-safe na PreparedStatement placeholder.
- **`<id>` tag required sa resultMap na may JOIN**: para hindi gumawa ng duplicate nested objects ang MyBatis bawat magkaparehong row.
- **`namespace` must equal the fully-qualified mapper interface path** — kung mali (typo sa package), mag-load ang XML pero hindi mabind sa interface, magreresulta ng runtime `BindingException`.
- **`int` vs `long`/`Long` inconsistency**: `Department.id` ay `int`, pero `Student`/`StudentM.id` ay `long`/`Long` — minor inconsistency sa project, pero dapat sundin ang existing type kapag ginagawa ng bagong DTO/method papunta sa ganitong models.
