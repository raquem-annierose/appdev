# Activity API Test Cases

base url: http://localhost:8080/activities

---

## 1. POST /activities – add activity

```
POST http://localhost:8080/activities
Content-Type: application/json
```

body:
```json
{
  "name": "Capstone",
  "description": "System development for capstone project"
}
```

expected output (201):
```json
{
  "id": 1,
  "name": "Capstone",
  "description": "System development for capstone project",
  "createdAt": "2026-04-11T08:00:00"
}
```

pag walang name sa body:
```json
{
  "name": "",
  "description": "walang name"
}
```
dapat 400 – "Name is required."

---

## 2. GET /activities – get all

```
GET http://localhost:8080/activities
```

expected (200):
```json
[
  {
    "id": 1,
    "name": "Capstone",
    "description": "System development for capstone project",
    "createdAt": "2026-04-11T08:00:00"
  }
]
```

pag wala pang nalagay: `[]`

---

## 3. GET /activities/{id} – get by id

```
GET http://localhost:8080/activities/1
```

expected (200):
```json
{
  "id": 1,
  "name": "Capstone",
  "description": "System development for capstone project",
  "createdAt": "2026-04-11T08:00:00"
}
```

pag mali ang id:
```
GET http://localhost:8080/activities/999
```
dapat 404 – "Activity with ID 999 not found."

---

## 4. PUT /activities/{id} – update activity

```
PUT http://localhost:8080/activities/1
Content-Type: application/json
```

body:
```json
{
  "name": "Capstone Mock 2",
  "description": "Mock defense 2 with panel"
}
```

expected (200):
```json
{
  "id": 1,
  "name": "Capstone Mock 2",
  "description": "Mock defense 2 with panel",
  "createdAt": "2026-04-11T08:00:00"
}
```

pag walang name:
- dapat 400 – "Name is required."

pag mali ang id:
- dapat 404 – "Activity with ID 999 not found."

---

## 5. DELETE /activities/{id} – delete activity

```
DELETE http://localhost:8080/activities/1
```

expected (200):
```
"Activity with ID 1 deleted successfully."
```

pag mali ang id:
```
DELETE http://localhost:8080/activities/999
```
dapat 404 – "Activity with ID 999 not found."

---

## Full Flow

| Step | Method | URL | Notes |
|------|--------|-----|-------|
| 1 | POST | `/activities` | add Capstone |
| 2 | GET | `/activities` | dapat makita yung Capstone |
| 3 | GET | `/activities/1` | get Capstone by id |
| 4 | PUT | `/activities/1` | i-update to Capstone Mock 2 |
| 5 | GET | `/activities/1` | confirm na na-update |
| 6 | DELETE | `/activities/1` | delete |
| 7 | GET | `/activities` | dapat wala na |
| 8 | DELETE | `/activities/999` | dapat 404 |
