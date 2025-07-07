# Backend Project Structure - Maven/Spring Boot

## Project Structure Overview

```
lms-backend/
├── api-gateway/                          # Spring Cloud Gateway
├── user-service/                         # User Management Microservice
├── course-service/                       # Course Management Microservice
├── enrollment-service/                   # Enrollment & Progress Tracking
├── assessment-service/                   # Assessment & Grading
├── notification-service/                 # Notification Management
├── shared-lib/                          # Shared DTOs and Utilities
├── docker/                              # Docker Configurations
├── k8s/                                 # Kubernetes Manifests
├── docs/                                # Documentation
└── scripts/                             # Build and Deployment Scripts
```

## Individual Microservice Structure

### 1. Course Service Structure

```
course-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── lms/
│   │   │           └── course/
│   │   │               ├── CourseServiceApplication.java
│   │   │               ├── config/
│   │   │               │   ├── DatabaseConfig.java
│   │   │               │   ├── SecurityConfig.java
│   │   │               │   ├── RabbitMQConfig.java
│   │   │               │   ├── RedisConfig.java
│   │   │               │   └── WebFluxConfig.java
│   │   │               ├── controller/
│   │   │               │   ├── CourseController.java
│   │   │               │   ├── ModuleController.java
│   │   │               │   └── ContentController.java
│   │   │               ├── service/
│   │   │               │   ├── CourseService.java
│   │   │               │   ├── ModuleService.java
│   │   │               │   ├── ContentService.java
│   │   │               │   ├── SearchService.java
│   │   │               │   └── FileStorageService.java
│   │   │               ├── repository/
│   │   │               │   ├── CourseRepository.java
│   │   │               │   ├── ModuleRepository.java
│   │   │               │   └── ContentRepository.java
│   │   │               ├── entity/
│   │   │               │   ├── Course.java
│   │   │               │   ├── CourseModule.java
│   │   │               │   └── CourseContent.java
│   │   │               ├── dto/
│   │   │               │   ├── request/
│   │   │               │   │   ├── CreateCourseRequest.java
│   │   │               │   │   ├── UpdateCourseRequest.java
│   │   │               │   │   └── SearchCourseRequest.java
│   │   │               │   ├── response/
│   │   │               │   │   ├── CourseResponse.java
│   │   │               │   │   ├── ModuleResponse.java
│   │   │               │   │   └── CourseListResponse.java
│   │   │               │   └── common/
│   │   │               │       ├── PageResponse.java
│   │   │               │       └── ApiResponse.java
│   │   │               ├── event/
│   │   │               │   ├── CourseCreatedEvent.java
│   │   │               │   ├── CoursePublishedEvent.java
│   │   │               │   └── ContentUploadedEvent.java
│   │   │               ├── exception/
│   │   │               │   ├── CourseNotFoundException.java
│   │   │               │   ├── UnauthorizedException.java
│   │   │               │   └── ValidationException.java
│   │   │               ├── util/
│   │   │               │   ├── ValidationUtils.java
│   │   │               │   ├── SecurityUtils.java
│   │   │               │   └── FileUtils.java
│   │   │               └── constant/
│   │   │                   ├── CourseStatus.java
│   │   │                   ├── ModuleType.java
│   │   │                   └── ErrorMessages.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       ├── db/
│   │       │   └── migration/
│   │       │       ├── V1__Create_courses_table.sql
│   │       │       ├── V2__Create_course_modules_table.sql
│   │       │       └── V3__Create_course_contents_table.sql
│   │       └── static/
│   │           └── swagger-ui/
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── lms/
│       │           └── course/
│       │               ├── CourseServiceApplicationTests.java
│       │               ├── controller/
│       │               │   └── CourseControllerTest.java
│       │               ├── service/
│       │               │   └── CourseServiceTest.java
│       │               ├── repository/
│       │               │   └── CourseRepositoryTest.java
│       │               └── integration/
│       │                   └── CourseIntegrationTest.java
│       └── resources/
│           ├── application-test.yml
│           └── test-data/
│               └── sample-courses.json
├── target/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── README.md
└── .gitignore
```

### 2. Maven Configuration (pom.xml)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>
    
    <groupId>com.lms</groupId>
    <artifactId>course-service</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <name>LMS Course Service</name>
    <description>Course Management Microservice for LMS</description>
    
    <properties>
        <java.version>17</java.version>
        <spring-cloud.version>2023.0.0</spring-cloud.version>
        <spring-boot.version>3.2.0</spring-boot.version>
        <r2dbc-postgresql.version>1.0.2.RELEASE</r2dbc-postgresql.version>
        <springdoc-openapi.version>2.2.0</springdoc-openapi.version>
        <testcontainers.version>1.19.3</testcontainers.version>
        <junit-jupiter.version>5.10.1</junit-jupiter.version>
    </properties>
    
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    
    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-r2dbc</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        
        <!-- Database -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>r2dbc-postgresql</artifactId>
            <version>${r2dbc-postgresql.version}</version>
        </dependency>
        
        <!-- Spring Cloud -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
        
        <!-- Messaging -->
        <dependency>
            <groupId>org.springframework.amqp</groupId>
            <artifactId>spring-rabbit</artifactId>
        </dependency>
        
        <!-- Redis -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
        </dependency>
        
        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.11.5</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>
        
        <!-- Documentation -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webflux-ui</artifactId>
            <version>${springdoc-openapi.version}</version>
        </dependency>
        
        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>io.projectreactor</groupId>
            <artifactId>reactor-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>${testcontainers.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>postgresql</artifactId>
            <version>${testcontainers.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <configuration>
                    <includes>
                        <include>**/*Test.java</include>
                        <include>**/*Tests.java</include>
                    </includes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### 3. Application Configuration

```yaml
# application.yml
spring:
  application:
    name: course-service
  profiles:
    active: dev
  cloud:
    discovery:
      enabled: true
    config:
      enabled: true
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/lms_courses
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:password}
  rabbitmq:
    host: ${RABBITMQ_HOST:localhost}
    port: ${RABBITMQ_PORT:5672}
    username: ${RABBITMQ_USERNAME:guest}
    password: ${RABBITMQ_PASSWORD:guest}
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${JWT_ISSUER_URI}
          jwk-set-uri: ${JWT_JWK_SET_URI}

server:
  port: ${SERVER_PORT:8082}

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always

logging:
  level:
    com.lms.course: DEBUG
    org.springframework.web: DEBUG
    org.springframework.security: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: method
```

### 4. Coding Conventions

#### 4.1 Package Naming
```
com.lms.{service-name}.{layer}
- com.lms.course.controller
- com.lms.course.service
- com.lms.course.repository
- com.lms.course.entity
- com.lms.course.dto
- com.lms.course.config
- com.lms.course.util
- com.lms.course.exception
```

#### 4.2 Class Naming
- **Controllers**: `{Entity}Controller` (e.g., `CourseController`)
- **Services**: `{Entity}Service` (e.g., `CourseService`)
- **Repositories**: `{Entity}Repository` (e.g., `CourseRepository`)
- **Entities**: `{Entity}` (e.g., `Course`)
- **DTOs**: `{Action}{Entity}{Type}` (e.g., `CreateCourseRequest`, `CourseResponse`)
- **Exceptions**: `{Entity}Exception` (e.g., `CourseNotFoundException`)

#### 4.3 Method Naming
```java
// Repository methods
findBy{Field}({Type} field)
findBy{Field}And{Field}({Type} field1, {Type} field2)
countBy{Field}({Type} field)
deleteBy{Field}({Type} field)

// Service methods
create{Entity}({CreateRequest} request)
update{Entity}({Id} id, {UpdateRequest} request)
delete{Entity}({Id} id)
get{Entity}ById({Id} id)
getAll{Entities}()
search{Entities}({SearchRequest} request)

// Controller methods
create{Entity}(@RequestBody {CreateRequest} request)
update{Entity}(@PathVariable {Id} id, @RequestBody {UpdateRequest} request)
delete{Entity}(@PathVariable {Id} id)
get{Entity}(@PathVariable {Id} id)
getAll{Entities}(@RequestParam Map<String, String> params)
```

#### 4.4 Variable Naming
```java
// Use camelCase for variables
String courseTitle;
UUID instructorId;
LocalDateTime createdAt;
List<CourseModule> modules;

// Use UPPER_SNAKE_CASE for constants
public static final String DEFAULT_STATUS = "DRAFT";
public static final int MAX_TITLE_LENGTH = 255;

// Use descriptive names
// Good
List<Course> publishedCourses = courseRepository.findByStatus(CourseStatus.PUBLISHED);

// Bad
List<Course> list = repo.findByStatus(status);
```

#### 4.5 File Organization
```
src/main/java/com/lms/course/
├── controller/           # REST controllers
├── service/             # Business logic
├── repository/          # Data access layer
├── entity/              # JPA entities
├── dto/                 # Data Transfer Objects
│   ├── request/         # Request DTOs
│   ├── response/        # Response DTOs
│   └── common/          # Shared DTOs
├── config/              # Configuration classes
├── event/               # Event classes
├── exception/           # Custom exceptions
├── util/                # Utility classes
└── constant/            # Constants and enums
```

### 5. Testing Structure

#### 5.1 Test Organization
```
src/test/java/com/lms/course/
├── controller/           # Controller tests
├── service/             # Service tests
├── repository/          # Repository tests
├── integration/         # Integration tests
└── util/                # Test utilities
```

#### 5.2 Test Naming
```java
// Test class naming
{Class}Test.java
{Class}IntegrationTest.java

// Test method naming
should{ExpectedBehavior}_when{Condition}()
test{MethodName}_with{Parameters}()
given{Setup}_when{Action}_then{ExpectedResult}()
```

#### 5.3 Test Examples
```java
@ExtendWith(MockitoExtension.class)
class CourseServiceTest {
    
    @Mock
    private CourseRepository courseRepository;
    
    @Mock
    private RabbitTemplate rabbitTemplate;
    
    @InjectMocks
    private CourseService courseService;
    
    @Test
    void shouldCreateCourse_whenValidRequestProvided() {
        // Given
        CreateCourseRequest request = new CreateCourseRequest("Test Course", "Description");
        UUID instructorId = UUID.randomUUID();
        Course savedCourse = new Course();
        savedCourse.setId(UUID.randomUUID());
        savedCourse.setTitle("Test Course");
        
        when(courseRepository.save(any(Course.class))).thenReturn(Mono.just(savedCourse));
        when(rabbitTemplate.convertAndSend(anyString(), anyString(), any())).thenReturn(null);
        
        // When
        Mono<Course> result = courseService.createCourse(request, instructorId);
        
        // Then
        StepVerifier.create(result)
                .expectNextMatches(course -> 
                    course.getTitle().equals("Test Course") &&
                    course.getInstructorId().equals(instructorId))
                .verifyComplete();
        
        verify(courseRepository).save(any(Course.class));
        verify(rabbitTemplate).convertAndSend(eq("course.events"), eq("course.created"), any());
    }
    
    @Test
    void shouldThrowException_whenCourseNotFound() {
        // Given
        UUID courseId = UUID.randomUUID();
        UUID instructorId = UUID.randomUUID();
        
        when(courseRepository.findById(courseId)).thenReturn(Mono.empty());
        
        // When & Then
        StepVerifier.create(courseService.updateCourse(courseId, new UpdateCourseRequest(), instructorId))
                .expectError(CourseNotFoundException.class)
                .verify();
    }
}
```

### 6. Docker Configuration

```dockerfile
# Dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests

# Create runtime image
FROM openjdk:17-jre-slim

WORKDIR /app

# Copy the built jar
COPY --from=0 /app/target/*.jar app.jar

# Create non-root user
RUN addgroup --system app && adduser --system --ingroup app app
USER app

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```yaml
# docker-compose.yml
version: '3.8'

services:
  course-service:
    build: .
    ports:
      - "8082:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - DB_HOST=postgres
      - RABBITMQ_HOST=rabbitmq
      - REDIS_HOST=redis
    depends_on:
      - postgres
      - rabbitmq
      - redis
    networks:
      - lms-network

  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: lms_courses
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - lms-network

  rabbitmq:
    image: rabbitmq:3-management
    ports:
      - "5672:5672"
      - "15672:15672"
    environment:
      RABBITMQ_DEFAULT_USER: guest
      RABBITMQ_DEFAULT_PASS: guest
    networks:
      - lms-network

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    networks:
      - lms-network

volumes:
  postgres_data:

networks:
  lms-network:
    driver: bridge
```

### 7. Git Configuration

```gitignore
# .gitignore
HELP.md
target/
!.mvn/wrapper/maven-wrapper.jar
!**/src/main/**/target/
!**/src/test/**/target/

### STS ###
.apt_generated
.classpath
.factorypath
.project
.settings
.springBeans
.sts4-cache

### IntelliJ IDEA ###
.idea
*.iws
*.iml
*.ipr

### NetBeans ###
/nbproject/private/
/nbbuild/
/dist/
/nbdist/
/.nb-gradle/
build/
!**/src/main/**/build/
!**/src/test/**/build/

### VS Code ###
.vscode/

### OS ###
.DS_Store
Thumbs.db

### Application ###
application-local.yml
application-prod.yml
*.log
logs/
```

This structure provides a comprehensive foundation for building scalable, maintainable Spring Boot microservices with proper separation of concerns, testing strategies, and deployment configurations. 