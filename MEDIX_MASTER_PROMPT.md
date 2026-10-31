You are helping me build "MediX", a Medicine Availability Finder desktop system, as a pure Java 25 project.

PROJECT IDENTITY
- Application name: MediX (use "MediX" in window titles, the About dialog, logs, README, and the OpenAPI title).
- Maven groupId: com.medix. Parent artifactId: medix-parent. Base package: com.medix.
- Server application class: com.medix.server.MediXServerApplication. Client entry point: com.medix.client.MediXApp.

ARCHITECTURE
- Maven multi-module project (Maven 3.9.9 or newer):
  - medix-common: shared DTOs (Java records), enums, validation annotations. Depends only on jakarta.validation-api and com.fasterxml.jackson.core:jackson-annotations (no Spring, no Jackson databind).
  - medix-client: Java Swing desktop app (JFrame-based).
  - medix-server: Spring Boot REST API.
- JAVA 25 (LTS) EVERYWHERE
  - Parent POM sets <maven.compiler.release>25</maven.compiler.release>; maven-compiler-plugin 3.14.0 or newer with <release>25</release>.
  - Add maven-enforcer-plugin rules: requireJavaVersion [25,) and requireMavenVersion [3.9.9,).
  - Use modern Java where it improves clarity: records (with compact constructors for validation/normalization), sealed interfaces, pattern matching for switch, record patterns, unnamed variables (_), text blocks, var, sequenced collections (getFirst/getLast/reversed), flexible constructor bodies (statements before super()/this()), Stream gatherers where they genuinely simplify code.
  - Do NOT use preview or incubator features (no --enable-preview, no StructuredTaskScope, no StableValue/Vector API).
  - Java 23+ no longer runs annotation processors implicitly: always declare them in annotationProcessorPaths (see below).
  - The client uses native code (FlatLaf on Windows): run it with --enable-native-access=ALL-UNNAMED to avoid restricted-method warnings.
  - Mockito on Java 25: configure the Mockito Java agent via maven-surefire-plugin argLine so tests run without dynamic-agent warnings.
- SERVER: latest stable Spring Boot 4.x (Spring Framework 7, Spring Security 7, Jakarta EE 11, Hibernate ORM 7 + hibernate-spatial, Jackson 3, JUnit Jupiter 6 as managed by Boot).
  - Remember Boot 4 uses modular starters (e.g. spring-boot-starter-flyway, spring-boot-starter-webmvc); use the Boot 4 starter names, not Boot 3 ones.
  - Enable virtual threads: spring.threads.virtual.enabled=true.
- JACKSON 3 in BOTH client and server so DTO serialization matches (tools.jackson.core:jackson-databind; Java time support is built in). Use one shared, identically configured JsonMapper setup (ISO-8601 dates, no timestamps, ignore unknown properties).
- Lombok 1.18.42 or newer (needed for Java 25), MapStruct 1.6.x, and lombok-mapstruct-binding 0.2.0, all declared explicitly in maven-compiler-plugin annotationProcessorPaths, with versions managed in the parent POM.
- Pick library versions confirmed compatible with Java 25 and Spring Boot 4 (e.g. springdoc-openapi 3.x for Boot 4, ShedLock 7.x, jjwt 0.13.x, Flyway + flyway-database-postgresql as managed by Boot). Keep all versions in the parent POM <properties>. If you are unsure whether a version supports Java 25 / Boot 4, say so explicitly instead of guessing.
- NO Docker anywhere. PostgreSQL (with PostGIS and pg_trgm extensions) and Redis-compatible server run as local services on Windows (localhost). For Redis on Windows, assume Memurai (Redis-compatible Windows service) or Redis in WSL, listening on localhost:6379.
- Databases: medix (dev) and medix_test (tests). Flyway migrations in medix-server/src/main/resources/db/migration (V1__..., V2__...). No Testcontainers; integration tests use the local medix_test database.
- Redis on localhost:6379: search-result caching, refresh tokens, search statistics, distributed locks. Key prefix "medix:".
- Spring Security 7 with stateless JWT (access + refresh tokens); roles: CUSTOMER, PHARMACY_OWNER, ADMIN.
- Scheduled jobs with @Scheduled + ShedLock (JDBC provider, shedlock table created by Flyway).
- CLIENT: Java Swing with JFrame/JDialog/JPanel classes written in code (no .form files, no GUI builder), FlatLaf (+ flatlaf-extras), MigLayout (miglayout-swing), java.net.http.HttpClient + Jackson 3, SwingWorker for all background work, JXMapViewer2 (OpenStreetMap tiles) for maps, JFreeChart for charts. All UI updates on the Event Dispatch Thread. Client is packaged as an executable fat JAR (maven-shade-plugin or similar); optionally a Windows installer via jpackage later.

BUILD ORDER: UI-FIRST
- The UI is built before the backend, against mock services.
- Swing UI code must NEVER call HttpClient directly. It only calls service interfaces in com.medix.client.api.service: AuthService, MedicineService, SearchService, PharmacyService, InventoryService, ReservationService, AlertService, AdminService, GeocodingService.
- All request/response types are records in medix-common (package com.medix.common.dto, enums in com.medix.common.enums). These DTOs are the contract; the backend must use them exactly. Never change a DTO without telling me what must change on the other side, and update API_CONTRACT.md.
- Implementations: com.medix.client.api.mock (in-memory, first) and com.medix.client.api.http (real REST, later). ServiceFactory picks one from "api.mode=mock|http" in client.properties (also holds server.url=http://localhost:8080).

USER FLOW
User searches "Medicine X" → sees nearby pharmacies → sees availability and quantity → reserves medicine.

FEATURES
Pharmacy registration, medicine inventory, search by medicine name, nearby pharmacy search, stock tracking, low-stock alerts, medicine reservation, expiry-date tracking, admin system.

CODING RULES
- Server packages (under com.medix.server): controller, service, repository, entity, mapper, config, exception, scheduler, security
- Client packages (under com.medix.client): ui.frame, ui.panel, ui.dialog, ui.component, ui.model, api.service, api.mock, api.http, session, util
- Constructor injection only, no field @Autowired
- Never expose JPA entities through the API; map entity ↔ DTO with MapStruct mappers only
- Global exception handler (@RestControllerAdvice) returning ApiError JSON (record in medix-common)
- Bean Validation on all request DTOs (jakarta.validation)
- Lombok only on JPA entities (never on records/DTOs, never @Data on entities)
- Always give COMPLETE files with full package and import lines, never "..." placeholders
- End every answer with: files created/changed, and exact steps to run/test this part (Windows commands: mvn, psql, and how to run the client JAR with java 25)
