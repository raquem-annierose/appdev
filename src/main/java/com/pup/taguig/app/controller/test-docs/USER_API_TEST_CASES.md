# User REST API — Test Cases

Base URL: `http://localhost:8080/user`

---

## 1. POST `/user/` — Add Student

**Request:**
```
POST http://localhost:8080/user/
Content-Type: application/json
```

**Body:**
```json
{
  "id": 6,
  "firstName": "Juan",
  "lastName": "Dela Cruz",
  "grade1": 88,
  "grade2": 92
}
```

**Expected Response:**
```json
{
  "id": 6,
  "firstName": "Juan",
  "lastName": "Dela Cruz",
  "grade1": 88,
  "grade2": 92
}
```

---

## 2. GET `/user/` — Get All Students (hardcoded list)

**Request:**
```
GET http://localhost:8080/user/
```

**Expected Response:**
```json
[
  { "id": 1, "firstName": "Annie", "lastName": "Raquem", "grade1": 85, "grade2": 90 },
  { "id": 2, "firstName": "Rose",  "lastName": "Raquem", "grade1": 95, "grade2": 97 },
  { "id": 3, "firstName": "Yowro", "lastName": "Raquem", "grade1": 90, "grade2": 85 },
  { "id": 4, "firstName": "Ann",   "lastName": "Raquem", "grade1": 90, "grade2": 85 },
  { "id": 5, "firstName": "Niera", "lastName": "Raquem", "grade1": 90, "grade2": 85 }
]
```

---

## 3. GET `/user/all` — Get All Students (from service)

> Only shows students added via POST.

**Request:**
```
GET http://localhost:8080/user/all
```

**Expected Response (after adding Juan via POST):**
```json
[
  { "id": 6, "firstName": "Juan", "lastName": "Dela Cruz", "grade1": 88, "grade2": 92 }
]
```

---

## 4. GET `/user/{id}` — Get Student by ID

**Request:**
```
GET http://localhost:8080/user/1
```

**Expected Response:**
```json
{
  "id": 1,
  "firstName": "Annie",
  "lastName": "Raquem",
  "grade1": 85,
  "grade2": 90
}
```

**Edge Case — ID not found:**
```
GET http://localhost:8080/user/999
```
Expected: `null` or empty body

---

## 5. GET `/user/filter` — Search by Last Name + First Name

**Request:**
```
GET http://localhost:8080/user/filter?lastName=Raquem&firstName=Rose
```

**Expected Response:**
```json
[
  { "id": 2, "firstName": "Rose", "lastName": "Raquem", "grade1": 95, "grade2": 97 }
]
```

**Edge Case — No match:**
```
GET http://localhost:8080/user/filter?lastName=Santos&firstName=Jose
```
Expected: `[]`

---

## 6. DELETE `/user/{id}` — Delete Student

> First, POST a student so there's something to delete.

**Request:**
```
DELETE http://localhost:8080/user/6
```

**Expected Response:**
```
true
```

**Verify deletion:**
```
GET http://localhost:8080/user/all
```
Expected: Juan is no longer in the list.

---

## Full Flow (in order)

| Step | Method | URL | Notes |
|------|--------|-----|-------|
| 1 | POST | `/user/` | Add Juan Dela Cruz |
| 2 | GET | `/user/all` | Confirm Juan is in service |
| 3 | GET | `/user/` | Check hardcoded list |
| 4 | GET | `/user/6` | Get Juan by ID |
| 5 | GET | `/user/filter?lastName=Dela Cruz&firstName=Juan` | Search by full name |
| 6 | DELETE | `/user/6` | Delete Juan |
| 7 | GET | `/user/all` | Confirm Juan is gone |
