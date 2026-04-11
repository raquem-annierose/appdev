# Activity REST API — Test Cases

Base URL: `http://localhost:8080/activities`

---

## 1. POST `/activities` — Create Activity

**Request:**
```
POST http://localhost:8080/activities
Content-Type: application/json
```

**Body:**
```json
{
  "name": "Swimming",
  "description": "Morning swim session"
}
```

**Expected Response — 201 Created:**
```json
{
  "id": 1,
  "name": "Swimming",
  "description": "Morning swim session",
  "createdAt": "2026-04-11T10:00:00"
}
```

**Edge Case — Missing name:**
```json
{
  "name": "",
  "description": "No name provided"
}
```
Expected: `400 Bad Request` — `"Name is required."`

---

## 2. GET `/activities` — Get All Activities

**Request:**
```
GET http://localhost:8080/activities
```

**Expected Response — 200 OK:**
```json
[
  {
    "id": 1,
    "name": "Swimming",
    "description": "Morning swim session",
    "createdAt": "2026-04-11T10:00:00"
  }
]
```

**Edge Case — No activities yet:**

Expected: `200 OK` — `[]`

---

## 3. GET `/activities/{id}` — Get Activity by ID

**Request:**
```
GET http://localhost:8080/activities/1
```

**Expected Response — 200 OK:**
```json
{
  "id": 1,
  "name": "Swimming",
  "description": "Morning swim session",
  "createdAt": "2026-04-11T10:00:00"
}
```

**Edge Case — ID not found:**
```
GET http://localhost:8080/activities/999
```
Expected: `404 Not Found` — `"Activity with ID 999 not found."`

---

## 4. PUT `/activities/{id}` — Update Activity

**Request:**
```
PUT http://localhost:8080/activities/1
Content-Type: application/json
```

**Body:**
```json
{
  "name": "Swimming Advanced",
  "description": "Evening swim session"
}
```

**Expected Response — 200 OK:**
```json
{
  "id": 1,
  "name": "Swimming Advanced",
  "description": "Evening swim session",
  "createdAt": "2026-04-11T10:00:00"
}
```

**Edge Case — Missing name:**
```json
{
  "name": "",
  "description": "Updated description"
}
```
Expected: `400 Bad Request` — `"Name is required."`

**Edge Case — ID not found:**
```
PUT http://localhost:8080/activities/999
```
Expected: `404 Not Found` — `"Activity with ID 999 not found."`

---

## 5. DELETE `/activities/{id}` — Delete Activity

**Request:**
```
DELETE http://localhost:8080/activities/1
```

**Expected Response — 200 OK:**
```
"Activity with ID 1 deleted successfully."
```

**Edge Case — ID not found:**
```
DELETE http://localhost:8080/activities/999
```
Expected: `404 Not Found` — `"Activity with ID 999 not found."`

---

## Full Flow (in order)

| Step | Method | URL | Body | Notes |
|------|--------|-----|------|-------|
| 1 | POST | `/activities` | `{ "name": "Swimming", "description": "..." }` | Create activity |
| 2 | POST | `/activities` | `{ "name": "Running", "description": "..." }` | Create second activity |
| 3 | GET | `/activities` | — | Should return both |
| 4 | GET | `/activities/1` | — | Get Swimming by ID |
| 5 | PUT | `/activities/1` | `{ "name": "Swimming Advanced", "description": "..." }` | Update Swimming |
| 6 | GET | `/activities/1` | — | Confirm update |
| 7 | DELETE | `/activities/1` | — | Delete Swimming |
| 8 | GET | `/activities` | — | Only Running should remain |
| 9 | DELETE | `/activities/999` | — | Should return 404 |
