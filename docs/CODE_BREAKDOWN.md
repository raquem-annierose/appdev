# User REST API - Detailed Code Breakdown & Explanation

---

## 📋 Table of Contents
1. [Architecture Overview](#architecture-overview)
2. [UserRestController - Method Breakdown](#userrestcontroller---method-breakdown)
3. [UserServiceImpl - Core Logic Explanation](#userserviceimpl---core-logic-explanation)
4. [Data Flow Diagrams](#data-flow-diagrams)

---

## Architecture Overview

### Three-Layer Architecture

```
┌─────────────────────────────────────┐
│   UserRestController               │  ← HTTP Requests/Responses
│   (API Endpoints)                  │
└──────────────────┬──────────────────┘
                   │
                   ├─→ Validates input
                   └─→ Calls Service layer
                   
┌──────────────────────────────────────┐
│   UserService Interface            │  ← Business Logic Contract
│   (Defines operations)              │
└──────────────────┬───────────────────┘
                   │
                   └─→ Implemented by UserServiceImpl

┌──────────────────────────────────────┐
│   UserServiceImpl                      │  ← Business Logic Implementation
│   (Database/Memory Operations)        │
└──────────────────────────────────────┘
```

---

## UserRestController - Method Breakdown

### Method 1: `getUsersById(Long id)`

**HTTP Method:** `GET`  
**URL Path:** `/{id}`  
**Purpose:** Retrieve a single student by their ID

#### Step-by-Step Logic:

```java
@GetMapping("/{id}")
public Student getUsersById(@PathVariable Long id) {
    // STEP 1: Check if students list exists (not null)
    if (Objects.nonNull(students)) {
        // STEP 2: Call service layer to find student by ID
        return userService.getUserById(id);
    }
    
    // STEP 3: If students list doesn't exist, print size (debugging)
    System.out.println(students.size());  // This will cause NullPointerException if students is null!
    
    // STEP 4: Return null if no students exist
    return null;
}
```

#### Walkthrough Example:

```
Input: GET /123

STEP 1: Is students list null? → NO (list exists)
        ↓
STEP 2: Call userService.getUserById(123)
        ↓
        Service searches through all students:
        - Student 1: ID = 100 (No match)
        - Student 2: ID = 123 (MATCH! ✓)
        ↓
STEP 3: Returns Student object with ID 123
        ↓
Output: {id: 123, firstName: "Juan", lastName: "Dela Cruz"}
```

#### Issues Found:
- ⚠️ **Line after if-statement:** If `students` is null, the code will crash at `students.size()`
- ✅ **Better approach:** Should return appropriate HTTP status code (404 Not Found) when student doesn't exist

---

### Method 2: `addStudent(Student student)`

**HTTP Method:** `POST`  
**URL Path:** `/`  
**Purpose:** Create and add a new student

#### Step-by-Step Logic:

```java
@PostMapping("/")
public Student addStudent(@RequestBody Student student) {
    // STEP 1: Initialize result as null
    Student result = null;
    
    // STEP 2: Check if incoming student object is not null
    if (Objects.nonNull(student)) {
        // STEP 3: Call service to add student
        result = userService.addUser(student);
        // Service returns the student with newly generated ID
    }
    
    // STEP 4: Return result (student with ID or null)
    return result;
}
```

#### Walkthrough Example:

```
Input: POST /
Body: {
  "firstName": "Juan",
  "lastName": "Dela Cruz"
}

STEP 1: Initialize result = null
        ↓
STEP 2: Is student object not null? → YES
        ↓
STEP 3: Call userService.addUser(student)
        ↓
        Service generates ID: 12345678901
        Service sets: student.setId(12345678901)
        Service adds to list: students.add(student)
        Service returns: student
        ↓
STEP 4: result now contains the student with ID
        ↓
Output: {id: 12345678901, firstName: "Juan", lastName: "Dela Cruz"}
```

#### What Happens:
| Scenario | Result |
|----------|--------|
| Valid student sent | Returns student with auto-generated ID |
| Null student sent | Returns null |
| Empty JSON sent | Returns null |

---

### Method 3: `getAllPosted()`

**HTTP Method:** `GET`  
**URL Path:** `/all`  
**Purpose:** Retrieve all students from the system

#### Step-by-Step Logic:

```java
@GetMapping("/all")
public List<Student> getAllPosted() {
    // STEP 1: Call service to retrieve all students
    return userService.retrieveAllStudent();
}
```

#### Walkthrough Example:

```
Input: GET /all

STEP 1: Call userService.retrieveAllStudent()
        ↓
        Service returns entire students list:
        [
          {id: 100, firstName: "Juan", lastName: "Dela Cruz"},
          {id: 101, firstName: "Maria", lastName: "Santos"},
          {id: 102, firstName: "Pedro", lastName: "Reyes"}
        ]
        ↓
Output: List of all students (3 items)

OR if list is empty:

Output: [] (empty list)
```

#### What It Returns:
- ✅ All students in the in-memory list
- ✅ Empty list `[]` if no students exist
- ❌ Does NOT return null

---

### Method 4: `searchByName(String name, String firstName)`

**HTTP Method:** `GET`  
**URL Path:** `/filter`  
**Query Parameters:** `name` and `firstName`  
**Purpose:** Search students by name

#### Step-by-Step Logic:

```java
@GetMapping("/filter")
public List<Student> searchByName(
    @RequestParam String name, 
    @RequestParam String firstName) {
    
    // STEP 1: Check if both name and firstName are not null
    if (Objects.nonNull(name) && Objects.nonNull(firstName)) {
        // STEP 2: Call service to search
        return userService.searchByName(name, firstName);
    }
    
    // STEP 3: If parameters missing, return empty list
    return new ArrayList<>();
}
```

#### Walkthrough Example 1 - With Parameters:

```
Input: GET /filter?name=Juan&firstName=Dela

STEP 1: Is name NOT null? YES
        Is firstName NOT null? YES
        ↓
STEP 2: Call userService.searchByName("Juan", "Dela")
        ↓
        Service loops through students:
        - Check each student...
        - Student {firstName: "Juan", lastName: "Dela Cruz"}
        - "Juan" matches name parameter ✓
        - Add to results
        ↓
Output: [{id: 123, firstName: "Juan", lastName: "Dela Cruz"}]
```

#### Walkthrough Example 2 - Missing Parameters:

```
Input: GET /filter  (no query parameters)

STEP 1: Is name NOT null? NO (missing)
        ↓
STEP 2: Skip the if block
        ↓
STEP 3: Return empty list
        ↓
Output: [] (empty array)
```

#### What It Does:
| Scenario | Result |
|----------|--------|
| Both parameters provided | Returns matching students |
| Parameter missing | Returns empty list `[]` |
| No matches found | Returns empty list `[]` |

---

### Method 5: `deleteStudent(Long id)`

**HTTP Method:** `DELETE`  
**URL Path:** `/{id}`  
**Request Parameter:** `id`  
**Purpose:** Delete a student by ID

#### Step-by-Step Logic:

```java
@DeleteMapping("/{id}")
public boolean deleteStudent(@RequestParam("id") Long id) {
    // STEP 1: Call service to delete student
    return userService.deleteStudent(id);
    // Returns true if deleted, false if not found
}
```

#### Walkthrough Example 1 - Student Exists:

```
Input: DELETE /123?id=123

STEP 1: Call userService.deleteStudent(123)
        ↓
        Service loops through students:
        - Student 1: ID = 100 (No match)
        - Student 2: ID = 123 (MATCH! ✓)
        - Remove this student from list
        - Return true
        ↓
Output: true (deletion successful)
```

#### Walkthrough Example 2 - Student Not Found:

```
Input: DELETE /999?id=999

STEP 1: Call userService.deleteStudent(999)
        ↓
        Service loops through students:
        - Student 1: ID = 100 (No match)
        - Student 2: ID = 123 (No match)
        - Loop ends, no student found
        - Return false
        ↓
Output: false (student not found)
```

#### What It Returns:
| Scenario | Result |
|----------|--------|
| Student found & deleted | Returns `true` |
| Student not found | Returns `false` |

---

## UserServiceImpl - Core Logic Explanation

### 1. ID Generation (In `addUser` method)

**Why?** The system needs to assign unique IDs to each student.  
**How?** Uses system timestamp components to create a unique number.

```java
LocalDateTime date = LocalDateTime.now();  // Get current date & time

long id = date.getDayOfYear()           // Day 1-365
        + date.getYear()                 // Year (2026)
        + date.getMonthValue()           // Month 1-12
        + date.getDayOfMonth()           // Day 1-31
        + date.getDayOfWeek().getValue() // Day of week 1-7
        + date.getHour()                 // Hour 0-23
        + date.getMinute()               // Minute 0-59
        + date.getSecond()               // Second 0-59
        + date.getNano();                // Nanosecond

student.setId(id);  // Set calculated ID to student
students.add(student);  // Add to list
return student;  // Return student with ID
```

#### Example ID Generation:

```
If current date/time is: April 11, 2026 at 14:30:45.123456789

Calculation:
= 101 (day of year)
+ 2026 (year)
+ 4 (month)
+ 11 (day of month)
+ 4 (Friday)
+ 14 (hour)
+ 30 (minute)
+ 45 (second)
+ 123456789 (nanosecond)
= 123457470

ID = 123457470
```

---

### 2. Get Student by ID (In `getUserById` method)

**Purpose:** Find and return ONE student from the list

```java
@Override
public Student getUserById(Long id) {
    // LOOP through each student
    for (Student student : students) {
        
        // Check if current student's ID matches search ID
        if (student.getId().equals(id)) {
            return student;  // Found! Return immediately
        }
    }
    
    // If loop completes, student not found
    return null;
}
```

#### Walkthrough:

```
Input: Search for ID = 123

List: [Student(ID:100), Student(ID:123), Student(ID:150)]

Loop Iteration 1:
  Current student ID = 100
  Does 100 == 123? NO → Continue

Loop Iteration 2:
  Current student ID = 123
  Does 123 == 123? YES ✓
  Return this student
  (Stop searching)

Output: Student with ID 123
```

---

### 3. Retrieve All Students (In `retrieveAllStudent` method)

**Purpose:** Return the complete list of all students

```java
@Override
public List<Student> retrieveAllStudent() {
    return students;  // Simply return the entire list
}
```

| State | Returns |
|-------|---------|
| List has 5 students | List of 5 students |
| List is empty | Empty list `[]` |
| List is null | null (problematic!) |

---

### 4. Search by Last Name

**Purpose:** Find all students with a specific last name

```java
@Override
public List<Student> searchByLastName(String lastName) {
    // Create empty results list
    List<Student> result = new ArrayList<>();
    
    // LOOP through each student
    for (Student student : students) {
        
        // Compare last names (case-insensitive)
        if (lastName.equalsIgnoreCase(student.getLastName())) {
            result.add(student);  // Match found, add to results
        }
    }
    
    // Return all matching students
    return result;
}
```

#### Walkthrough Example:

```
Input: Search for lastName = "SANTOS"

List: [
  Student(firstName: Juan, lastName: Dela Cruz),
  Student(firstName: Maria, lastName: Santos),
  Student(firstName: Pedro, lastName: Santos),
  Student(firstName: Rosa, lastName: Reyes)
]

Loop:
  Student 1: "Dela Cruz".equalsIgnoreCase("SANTOS")? NO
  Student 2: "Santos".equalsIgnoreCase("SANTOS")? YES ✓ → Add to result
  Student 3: "Santos".equalsIgnoreCase("SANTOS")? YES ✓ → Add to result
  Student 4: "Reyes".equalsIgnoreCase("SANTOS")? NO

Output: [
  Student(firstName: Maria, lastName: Santos),
  Student(firstName: Pedro, lastName: Santos)
]
```

---

### 5. Search by Name (In `searchByName` method)

**Purpose:** Find students by first or last name (flexible search)

```java
@Override
public List<Student> searchByName(String name, String firstName) {
    List<Student> result = new ArrayList<>();
    
    for (Student student : students) {
        
        // Check THREE conditions (any match = add to results):
        
        // Condition 1: Does 'name' match first name?
        if (name.equalsIgnoreCase(student.getFirstName())) {
            result.add(student);
        }
        
        // Condition 2: Does 'name' match last name?
        else if (name.equalsIgnoreCase(student.getLastName())) {
            result.add(student);
        }
        
        // Condition 3: Does 'name' match full name (first + last)?
        else if (name.equalsIgnoreCase(student.getFirstName() + " " + student.getLastName())) {
            result.add(student);
        }
    }
    
    return result;
}
```

#### Walkthrough Example:

```
Input: searchByName("Juan", parameter2)

List: [
  Student(firstName: Juan, lastName: Dela Cruz),
  Student(firstName: Maria, lastName: Santos),
  Student(firstName: Juan, lastName: Reyes)
]

Loop:

Student 1: firstName="Juan", lastName="Dela Cruz"
  ├─ "Juan".equalsIgnoreCase("Juan")? YES ✓ → Add to result
  ├─ Skip else conditions
  └─ Continue to next student

Student 2: firstName="Maria", lastName="Santos"
  ├─ "Juan".equalsIgnoreCase("Maria")? NO
  ├─ else if "Juan".equalsIgnoreCase("Santos")? NO
  ├─ else if "Juan".equalsIgnoreCase("Maria Santos")? NO
  └─ Don't add, continue

Student 3: firstName="Juan", lastName="Reyes"
  ├─ "Juan".equalsIgnoreCase("Juan")? YES ✓ → Add to result
  └─ Continue to next student

Output: [
  Student(firstName: Juan, lastName: Dela Cruz),
  Student(firstName: Juan, lastName: Reyes)
]
```

---

### 6. Delete Student (In `deleteStudent` method)

**Purpose:** Remove a student from the list by ID

```java
@Override
public boolean deleteStudent(Long id) {
    
    // LOOP through each student
    for (Student student : students) {
        
        // Check if this student's ID matches
        if (student.getId().equals(id)) {
            students.remove(student);  // Remove from list
            return true;                // Return success
        }
    }
    
    // If loop completes, student not found
    return false;
}
```

#### Walkthrough Example 1 - Successful Delete:

```
Input: deleteStudent(123)

List before: [Student(ID:100), Student(ID:123), Student(ID:150)]

Loop Iteration 1:
  Current student ID = 100
  Does 100 == 123? NO → Continue

Loop Iteration 2:
  Current student ID = 123
  Does 123 == 123? YES ✓
  Remove this student from list
  Return true
  STOP searching

List after: [Student(ID:100), Student(ID:150)]

Output: true
```

#### Walkthrough Example 2 - Not Found:

```
Input: deleteStudent(999)

List: [Student(ID:100), Student(ID:123), Student(ID:150)]

Loop:
  Student ID = 100, 999 == 100? NO
  Student ID = 123, 999 == 123? NO
  Student ID = 150, 999 == 150? NO
  (Loop ends)

Output: false (not found)
```

---

## Data Flow Diagrams

### Flow 1: Add New Student

```
Request: POST /
Body: {firstName: "Juan", lastName: "Dela Cruz"}
        ↓
UserRestController.addStudent()
  ├─ Validate: Is student not null? ✓
  ├─ Call: userService.addUser(student)
  │   ↓
  │   UserServiceImpl.addUser()
  │   ├─ Generate ID from timestamp
  │   ├─ Set student.id = generated ID
  │   ├─ Add to students list
  │   └─ Return student
  └─ Return student with ID
         ↓
Response: {id: 123457470, firstName: "Juan", lastName: "Dela Cruz"}
HTTP 200 OK
```

---

### Flow 2: Get Student by ID

```
Request: GET /123
        ↓
UserRestController.getUsersById(123)
  ├─ Check: Is students list not null? ✓
  ├─ Call: userService.getUserById(123)
  │   ↓
  │   UserServiceImpl.getUserById(123)
  │   ├─ Loop through students
  │   ├─ Find student with ID = 123
  │   └─ Return student
  └─ Return student
         ↓
Response: {id: 123, firstName: "Juan", lastName: "Dela Cruz"}
HTTP 200 OK
```

---

### Flow 3: Delete Student

```
Request: DELETE /123?id=123
        ↓
UserRestController.deleteStudent(123)
  ├─ Call: userService.deleteStudent(123)
  │   ↓
  │   UserServiceImpl.deleteStudent(123)
  │   ├─ Loop through students
  │   ├─ Find student with ID = 123
  │   ├─ Remove from list
  │   └─ Return true
  └─ Return true
         ↓
Response: true
HTTP 200 OK
```

---

## Summary Table

| Operation | Controller Method | Service Method | Returns | Purpose |
|-----------|-------------------|-----------------|---------|---------|
| Get by ID | `getUsersById()` | `getUserById()` | Single Student | Find one student |
| Add | `addStudent()` | `addUser()` | Student with ID | Create new student |
| Get All | `getAllPosted()` | `retrieveAllStudent()` | List<Student> | Get all students |
| Search by Name | `searchByName()` | `searchByName()` | List<Student> | Find matching students |
| Delete | `deleteStudent()` | `deleteStudent()` | boolean | Remove student |
