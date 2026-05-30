# Diagram Options — READ + WRITE Flow (kumpletong layers)

Dalawang paraan, pero **kompleto na lahat ng layer**: Controller → Service **interface** → Service Impl → Mapper **interface** → Mapper XML → Database, kasama ang **Model (`StudentM`)** na siyang object na dumadaan/pinupunan.

> 📦 = ang Model (`StudentM`) — ito ang basket na hawak ng data habang naglalakbay.

---

## OPTION 1 — Hiwalay (dalawang `sequenceDiagram`)

### 1A. READ — `GET /user/1` (database → balik sa client)

```mermaid
sequenceDiagram
    actor UI as 🖥️ Client
    participant C as Controller
    participant S as Service (interface)
    participant I as Service Impl
    participant M as Mapper (interface)
    participant X as Mapper XML
    participant DB as 🗄️ Database

    UI->>C: GET /user/1
    C->>S: getUserById(1)
    Note over S,I: tawag sa interface,<br/>tumatakbo sa Impl (Spring DI)
    S-->>I: 
    I->>M: studentMapper.getUserById(1)
    Note over M,X: namespace + id="getUserById"
    M-->>X: 
    X->>DB: SELECT ... WHERE id = #{id}
    DB-->>X: rows
    Note over X: resultMap fills 📦 StudentM<br/>first_name → firstName
    X-->>I: 📦 StudentM (+ Department)
    Note over I: toDTO(StudentM) → ResponseDTO
    I-->>C: StudentResponseDTO
    Note over C: Jackson → JSON
    C-->>UI: JSON
```

### 1B. WRITE — `POST /user/` (client nagbigay ng data → database)

```mermaid
sequenceDiagram
    actor UI as 🖥️ Client
    participant C as Controller
    participant S as Service (interface)
    participant I as Service Impl
    participant M as Mapper (interface)
    participant X as Mapper XML
    participant DB as 🗄️ Database

    UI->>C: POST /user/ { ... } (JSON)
    Note over C: Jackson → StudentRequestDTO
    C->>S: insertStudent(requestDTO)
    Note over S,I: tawag sa interface,<br/>tumatakbo sa Impl (Spring DI)
    S-->>I: 
    Note over I: RequestDTO → 📦 StudentM
    I->>M: studentMapper.insertStudent(📦 StudentM)
    Note over M,X: namespace + id="insertStudent"
    M-->>X: 
    X->>DB: INSERT INTO student VALUES (#{firstName}, ...)
    DB-->>X: bagong auto-generated id
    Note over X: useGeneratedKeys: id → 📦 StudentM.id
    X-->>I: id naka-set sa 📦 StudentM
    I-->>C: return id
    C-->>UI: JSON: id
```

**Pros:** napakalinaw, kompleto ang layers, madaling sundan.
**Cons:** dalawang diagram.

---

## OPTION 2 — Pinagsama (isang color-coded `flowchart`)

> 🟢 Berde solid = WRITE (pababa). 🔵 Asul dotted = READ (paakyat).
> Ang **`StudentM` (📦)** ay HINDI istasyon — siya ang **cargo/produkto** na nilikha ng XML at sumasakay sa return path paakyat. Kaya nakadikit siya sa **return arrows**, hindi sa gitna.

> Ang mga **kahon na hugis-kapsula (🟣)** = **lalagyanan/template lang** (POJO): `RequestDTO`, `StudentM`, `ResponseDTO`. Hindi sila istasyon — pinupunan lang sila sa isang layer, tapos dinadala.

```mermaid
flowchart TD
    UI["🖥️ Client"]
    C["Controller<br/>UserRestController"]
    S["Service interface<br/>StudentService"]
    I["Service Impl<br/>StudentServiceImpl"]
    M["Mapper interface<br/>StudentMapper<br/>(doorway lang — walang logic)"]
    X["Mapper XML<br/>StudentMapper.xml<br/>(dito pinupunan ang basket)"]
    DB[("🗄️ Database")]

    %% --- mga LALAGYANAN / template (POJO) ---
    REQ(["📥 StudentRequestDTO<br/>lalagyanan ng INPUT"])
    SM(["📦 StudentM<br/>lalagyanan ng DB data"])
    RES(["📤 StudentResponseDTO<br/>lalagyanan ng OUTPUT"])

    %% WRITE path (pababa) — index 0-5
    UI -->|"POST: JSON"| C
    C -->|"insertStudent(...)"| S
    S -->|"call → Impl (Spring DI)"| I
    I -->|"dala: StudentM"| M
    M -->|"proxy: namespace + id"| X
    X -->|"INSERT VALUES (#{...})"| DB

    %% READ path (paakyat) — index 6-11
    DB -.->|"rows / ResultSet"| X
    X -.->|"dala: StudentM"| M
    M -.->|"return: StudentM"| I
    I -.->|"dala: ResponseDTO"| S
    S -.->|"ResponseDTO"| C
    C -.->|"Jackson → JSON"| UI

    %% --- saan PINUPUNAN ang bawat lalagyanan (index 12-16) ---
    C -. "Jackson pumupuno (JSON→)" .- REQ
    I -. "WRITE: kinokopya mula REQ" .- SM
    X -. "READ: resultMap pumupuno<br/>(type = StudentM)" .- SM
    I -. "READ: toDTO pumupuno" .- RES
    C -. "Jackson basa (→JSON)" .- RES

    classDef basket fill:#f3e5f5,stroke:#9c27b0,stroke-width:2px,color:#4a148c;
    class REQ,SM,RES basket;

    linkStyle 0,1,2,3,4,5 stroke:#2e7d32,stroke-width:2px
    linkStyle 6,7,8,9,10,11 stroke:#1565c0,stroke-width:2px
    linkStyle 12,13,14,15,16 stroke:#9c27b0,stroke-width:1px,stroke-dasharray:4
```

**Legend:**
- 🟢 **Green solid (pababa)** = WRITE / `POST`.
- 🔵 **Blue dotted (paakyat)** = READ / `GET`.
- 🟣 **Purple capsule** = **lalagyanan/template** (POJO). Ang dashed purple na linya ay nagtuturo **saan pinupunan** ang bawat basket.

### Bakit kahon (lalagyanan) ang DTO at StudentM?

Lahat sila ay **POJO = basket lang ng data** (fields + getters/setters). Hindi sila gumagawa ng logic; **pinupunan** lang sila:
- **`StudentRequestDTO`** — pinupunan ni **Jackson** mula sa papasok na JSON.
- **`StudentM`** — pinupunan ng **XML** (READ) o ng **Impl** (WRITE).
- **`StudentResponseDTO`** — pinupunan ng **Impl** (`toDTO`), babasahin ni Jackson para gawing JSON.

### Paano "kumukuha ng lalagyanan" ang XML sa Model?

Sa READ, **hindi gumagawa ang XML ng sarili nitong klase** — **hinihiram/kinukuha** lang nito ang hugis ng lalagyanan mula sa **Model (`StudentM`)** sa pamamagitan ng `type` sa resultMap:

```xml
<resultMap id="studentResultMap" type="com.pup.taguig.app.model.StudentM">
    <result column="first_name" property="firstName"/>
    ...
</resultMap>
```

- `type="...StudentM"` = **"ito ang lalagyanan na gagamitin ko."** Dito tumitingin ang MyBatis kung anong basket ang bubuuin (`new StudentM()`).
- `property="firstName"` = **"ito ang slot sa basket na pupunan ko"** (via setter `setFirstName(...)`).
- `column="first_name"` = **"galing dito sa DB ang ilalagay."**

💡 Kaya: ang **Model (`StudentM`)** ang nagbibigay ng **disenyo ng lalagyanan**; ang **XML** ang **pumupuno** nito gamit ang DB data. Walang logic ang model — basket lang siya na tinutukoy ng XML.

**Bakit walang "model" box sa gitna ng interface at XML?** Kasi ang `StudentM` ay ang **dala** (cargo), hindi istasyon. Nilikha ito sa XML (gamit ang disenyo galing Model), dala paakyat (XML → interface → impl). Ang **Mapper interface** ay daanan lang — `StudentM getUserById(...)` ang signature, pero **XML** ang aktwal na pumupuno.

**Pros:** isang diagram, kompleto ang layers + model.
**Cons:** mas siksik; sundan ang kulay.

---

## Paghahambing

| | Option 1 (hiwalay) | Option 2 (pinagsama) |
|---|---|---|
| Layers | kompleto (7 participant + model sa note) | kompleto (8 node kasama model) |
| Linaw | ⭐⭐⭐ pinaka-malinaw | ⭐⭐ ok, basta sundan kulay |
| Laki | mahaba (2 diagram) | compact (1 diagram) |
| Best para sa | pag-aaral / presentation | quick reference |

➡️ **Sabihin mo lang kung alin** (o pareho — Option 2 overview sa taas, Option 1 detalye sa baba), ipapasok ko sa `MYBATIS_FLOW.md`.
