# Maven Commands Guide

Maven ay isang build automation tool na ginagamit para sa Java projects. Narito ang mga essential commands:

## Basic Commands

### 1. **mvn clean**
- **Purpose:** Burahin ang `target/` folder (compiled files, artifacts)
- **Usage:** `mvn clean`
- **Kailan gamitin:** Bago mag-compile bago gumawa ng fresh build

```bash
mvn clean
```

---

### 2. **mvn compile**
- **Purpose:** I-compile ang Java source code sa target/classes/
- **Usage:** `mvn compile`
- **Kailan gamitin:** Kapag gusto mo lang mag-check ng build errors

```bash
mvn compile
```

---

### 3. **mvn test**
- **Purpose:** I-run ang lahat ng unit tests sa project
- **Usage:** `mvn test`
- **Kailan gamitin:** Para ma-verify na ang code ay gumagana ng correcty

```bash
mvn test
```

---

### 4. **mvn package**
- **Purpose:** Gumawa ng JAR o WAR file mula sa compiled code
- **Usage:** `mvn package`
- **Output:** `target/demo-0.0.1-SNAPSHOT.jar`

```bash
mvn package
```

---

### 5. **mvn install**
- **Purpose:** I-install ang package sa local Maven repository
- **Usage:** `mvn install`
- **Kailan gamitin:** Kapag ready na ang artifact para sa production

```bash
mvn install
```

---

## Combined Commands (Most Used)

### **mvn clean package**
```bash
mvn clean package
```
- Burahin ang target folder + gumawa ng JAR file
- **Most common** command for building

### **mvn clean install**
```bash
mvn clean install
```
- Burahin + mag-install sa local repository

### **mvn clean compile**
```bash
mvn clean compile
```
- Burahin + mag-compile lang

---

## Spring Boot Specific Commands

### **mvn spring-boot:run**
- **Purpose:** I-run ang Spring Boot application directly
- **Usage:** `mvn spring-boot:run`
- **Kailan gamitin:** Para sa development/testing

```bash
mvn spring-boot:run
```

---

### **mvn dependency:tree**
- **Purpose:** Makita ang dependency hierarchy
- **Usage:** `mvn dependency:tree`
- **Output:** Shows all dependencies at transitive dependencies

```bash
mvn dependency:tree
```

---

## For This Project (SpringBootWeb1Application)

### Build at Run the App
```bash
# Option 1: Build and run with Maven
mvn clean package
java -jar target/demo-0.0.1-SNAPSHOT.jar

# Option 2: Run directly without building JAR
mvn spring-boot:run

# Option 3: Clean at mag-compile lang
mvn clean compile
```

---

## Common Errors at Solutions

| Error | Solution |
|-------|----------|
| "BUILD FAILURE" | Run `mvn clean compile` para makita ang exact error |
| "Tests failed" | Run `mvn test` para sa detailed test results |
| "Plugin not found" | Run `mvn dependency:resolve` |
| "Java version mismatch" | Check pom.xml java.version property |

---

## Project Structure (appdev)

```
appdev/
├── pom.xml                 (Maven configuration)
├── src/
│   ├── main/java/          (Source code)
│   ├── main/resources/     (Configuration files)
│   └── test/java/          (Test files)
└── target/                 (Generated files after build)
```

Para sa project na ito, ang Java version ay **17** at Spring Boot ay **3.2.3** (makikita sa pom.xml)
