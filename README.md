# MediX

MediX is a Medicine Availability Finder desktop system: search for a medicine, see nearby pharmacies with
stock and quantity, and reserve it.

| Module         | What it is                                               |
|----------------|----------------------------------------------------------|
| `medix-common` | Shared DTO records, enums, validation (the API contract) |
| `medix-client` | Java Swing desktop app (FlatLaf, MigLayout)              |
| `medix-server` | Spring Boot 4 REST API (added in Part 8)                 |

## 1. Install JDK 25

1. Download and install a JDK 25 build (Oracle JDK 25, Eclipse Temurin 25, or Microsoft Build of OpenJDK 25).
2. Set `JAVA_HOME` to the install folder, e.g. `C:\Program Files\Java\jdk-25.0.4.1`
   (Windows: *Settings → System → About → Advanced system settings → Environment Variables*).
3. Put `%JAVA_HOME%\bin` **first** in `Path`, above any older Java (such as `C:\Program Files\Java\jre-1.8`
   or `C:\Program Files\Common Files\Oracle\Java\javapath`).
4. Install Maven 3.9.9 or newer and add its `bin` folder to `Path`.

## 2. Verify

Open a **new** terminal and run:

```
java -version
mvn -v
```

Both must report Java **25**. If `mvn -v` shows `Java version: 1.8...`, `JAVA_HOME` is still wrong.
The build fails fast with a clear message if it is not run on JDK 25+.

## 3. Build

From the project root:

```
mvn clean package
```

This produces the runnable client jar `medix-client/target/medix-client.jar`.

## 4. Run the client

```
java -jar medix-client/target/medix-client.jar
```

The jar manifest already contains `Enable-Native-Access: ALL-UNNAMED`, so FlatLaf's native Windows
library loads without warnings.

### Configuration

`medix-client/src/main/resources/client.properties`:

```
api.mode=mock                       # mock = in-memory data, http = real server
server.url=http://localhost:8080
```

Any key can be overridden at launch without rebuilding:

```
java -Dapi.mode=http -Dserver.url=http://localhost:8080 -jar medix-client/target/medix-client.jar
```
