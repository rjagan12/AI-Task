# AI-Powered Learning Management System - Backend Implementation

## Phase 1: Project Setup and Core Infrastructure

### 1.1 Project Structure

```
lms-backend/
├── api-gateway/                 # Spring Cloud Gateway
├── user-service/               # User management microservice
├── course-service/             # Course management microservice
├── enrollment-service/         # Enrollment and progress tracking
├── assessment-service/         # Assessment and grading
├── notification-service/       # Notification management
├── shared-lib/                # Shared utilities and DTOs
├── docker/                    # Docker configurations
├── k8s/                      # Kubernetes manifests
└── docs/                     # Documentation
```

### 1.2 Parent POM Configuration

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.lms</groupId>
    <artifactId>lms-backend</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>
    
    <modules>
        <module>api-gateway</module>
        <module>user-service</module>
        <module>course-service</module>
        <module>enrollment-service</module>
        <module>assessment-service</module>
        <module>notification-service</module>
        <module>shared-lib</module>
    </modules>
    
    <properties>
        <java.version>17</java.version>
        <spring-boot.version>3.2.0</spring-boot.version>
        <spring-cloud.version>2023.0.0</spring-cloud.version>
        <spring-cloud-gateway.version>4.0.0</spring-cloud-gateway.version>
    </properties>
    
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

## Phase 2: Core Implementation

### Task 1: Course Management Service

#### 2.1 Course Service Dependencies

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-r2dbc</artifactId>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>r2dbc-postgresql</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.amqp</groupId>
        <artifactId>spring-rabbit</artifactId>
    </dependency>
</dependencies>
```

#### 2.2 Course Entity Model

```java
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private String instructorId;
    
    @Enumerated(EnumType.STRING)
    private CourseStatus status;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Version
    private Long version;
    
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<CourseModule> modules = new ArrayList<>();
    
    // Getters, setters, constructors
}

@Entity
@Table(name = "course_modules")
public class CourseModule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Column(nullable = false)
    private Integer orderIndex;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;
    
    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL)
    private List<CourseContent> contents = new ArrayList<>();
    
    // Getters, setters, constructors
}
```

#### 2.3 Course Repository

```java
@Repository
public interface CourseRepository extends ReactiveCrudRepository<Course, Long> {
    
    Flux<Course> findByInstructorId(String instructorId);
    
    Flux<Course> findByStatus(CourseStatus status);
    
    @Query("SELECT c FROM Course c WHERE " +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Flux<Course> searchByTitleOrDescription(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT c FROM Course c WHERE c.status = 'PUBLISHED' " +
           "ORDER BY c.createdAt DESC LIMIT :limit")
    Flux<Course> findLatestPublishedCourses(@Param("limit") int limit);
}
```

#### 2.4 Course Service Implementation

```java
@Service
@Transactional
public class CourseService {
    
    private final CourseRepository courseRepository;
    private final CourseModuleRepository moduleRepository;
    private final RabbitTemplate rabbitTemplate;
    private final FileStorageService fileStorageService;
    
    public CourseService(CourseRepository courseRepository,
                       CourseModuleRepository moduleRepository,
                       RabbitTemplate rabbitTemplate,
                       FileStorageService fileStorageService) {
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.fileStorageService = fileStorageService;
    }
    
    public Mono<Course> createCourse(CreateCourseRequest request, String instructorId) {
        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setInstructorId(instructorId);
        course.setStatus(CourseStatus.DRAFT);
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
        
        return courseRepository.save(course)
                .doOnSuccess(savedCourse -> {
                    // Publish course created event
                    CourseCreatedEvent event = new CourseCreatedEvent(
                        savedCourse.getId(),
                        savedCourse.getTitle(),
                        instructorId
                    );
                    rabbitTemplate.convertAndSend("course.events", "course.created", event);
                });
    }
    
    public Mono<Course> updateCourse(Long courseId, UpdateCourseRequest request, String instructorId) {
        return courseRepository.findById(courseId)
                .filter(course -> course.getInstructorId().equals(instructorId))
                .switchIfEmpty(Mono.error(new CourseNotFoundException("Course not found or access denied")))
                .flatMap(course -> {
                    course.setTitle(request.getTitle());
                    course.setDescription(request.getDescription());
                    course.setUpdatedAt(LocalDateTime.now());
                    return courseRepository.save(course);
                });
    }
    
    public Mono<Course> publishCourse(Long courseId, String instructorId) {
        return courseRepository.findById(courseId)
                .filter(course -> course.getInstructorId().equals(instructorId))
                .switchIfEmpty(Mono.error(new CourseNotFoundException("Course not found or access denied")))
                .flatMap(course -> {
                    course.setStatus(CourseStatus.PUBLISHED);
                    course.setUpdatedAt(LocalDateTime.now());
                    return courseRepository.save(course);
                })
                .doOnSuccess(course -> {
                    // Publish course published event
                    CoursePublishedEvent event = new CoursePublishedEvent(
                        course.getId(),
                        course.getTitle(),
                        instructorId
                    );
                    rabbitTemplate.convertAndSend("course.events", "course.published", event);
                });
    }
    
    public Flux<Course> searchCourses(String searchTerm, int page, int size) {
        return courseRepository.searchByTitleOrDescription(searchTerm)
                .skip(page * size)
                .take(size);
    }
    
    public Mono<CourseModule> addModule(Long courseId, CreateModuleRequest request, String instructorId) {
        return courseRepository.findById(courseId)
                .filter(course -> course.getInstructorId().equals(instructorId))
                .switchIfEmpty(Mono.error(new CourseNotFoundException("Course not found or access denied")))
                .flatMap(course -> {
                    CourseModule module = new CourseModule();
                    module.setTitle(request.getTitle());
                    module.setContent(request.getContent());
                    module.setOrderIndex(request.getOrderIndex());
                    module.setCourse(course);
                    return moduleRepository.save(module);
                });
    }
}
```

#### 2.5 Course Controller

```java
@RestController
@RequestMapping("/api/v1/courses")
@Validated
public class CourseController {
    
    private final CourseService courseService;
    private final JwtTokenProvider jwtTokenProvider;
    
    public CourseController(CourseService courseService, JwtTokenProvider jwtTokenProvider) {
        this.courseService = courseService;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public Mono<ResponseEntity<CourseResponse>> createCourse(
            @Valid @RequestBody CreateCourseRequest request,
            @RequestHeader("Authorization") String token) {
        
        String instructorId = jwtTokenProvider.getUserIdFromToken(token);
        
        return courseService.createCourse(request, instructorId)
                .map(course -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(CourseResponse.from(course)));
    }
    
    @GetMapping("/{courseId}")
    public Mono<ResponseEntity<CourseResponse>> getCourse(@PathVariable Long courseId) {
        return courseService.getCourseById(courseId)
                .map(course -> ResponseEntity.ok(CourseResponse.from(course)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
    
    @PutMapping("/{courseId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public Mono<ResponseEntity<CourseResponse>> updateCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody UpdateCourseRequest request,
            @RequestHeader("Authorization") String token) {
        
        String instructorId = jwtTokenProvider.getUserIdFromToken(token);
        
        return courseService.updateCourse(courseId, request, instructorId)
                .map(course -> ResponseEntity.ok(CourseResponse.from(course)));
    }
    
    @PostMapping("/{courseId}/publish")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public Mono<ResponseEntity<Void>> publishCourse(
            @PathVariable Long courseId,
            @RequestHeader("Authorization") String token) {
        
        String instructorId = jwtTokenProvider.getUserIdFromToken(token);
        
        return courseService.publishCourse(courseId, instructorId)
                .then(Mono.just(ResponseEntity.ok().build()));
    }
    
    @GetMapping("/search")
    public Mono<ResponseEntity<PageResponse<CourseResponse>>> searchCourses(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        return courseService.searchCourses(q, page, size)
                .map(CourseResponse::from)
                .collectList()
                .map(courses -> ResponseEntity.ok(new PageResponse<>(courses, page, size)));
    }
    
    @PostMapping("/{courseId}/modules")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public Mono<ResponseEntity<ModuleResponse>> addModule(
            @PathVariable Long courseId,
            @Valid @RequestBody CreateModuleRequest request,
            @RequestHeader("Authorization") String token) {
        
        String instructorId = jwtTokenProvider.getUserIdFromToken(token);
        
        return courseService.addModule(courseId, request, instructorId)
                .map(module -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ModuleResponse.from(module)));
    }
}
```

### Task 2: Student Progress Tracking

#### 2.6 Enrollment Service Implementation

```java
@Service
@Transactional
public class EnrollmentService {
    
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;
    private final CertificateService certificateService;
    
    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                           CourseRepository courseRepository,
                           UserRepository userRepository,
                           RabbitTemplate rabbitTemplate,
                           CertificateService certificateService) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.certificateService = certificateService;
    }
    
    public Mono<Enrollment> enrollStudent(Long courseId, String studentId) {
        return Mono.zip(
                courseRepository.findById(courseId),
                userRepository.findById(studentId)
        ).flatMap(tuple -> {
            Course course = tuple.getT1();
            User student = tuple.getT2();
            
            if (course.getStatus() != CourseStatus.PUBLISHED) {
                return Mono.error(new CourseNotAvailableException("Course is not available for enrollment"));
            }
            
            return enrollmentRepository.findByCourseIdAndStudentId(courseId, studentId)
                    .flatMap(existingEnrollment -> 
                        Mono.error(new AlreadyEnrolledException("Student is already enrolled")))
                    .switchIfEmpty(createEnrollment(course, student));
        });
    }
    
    private Mono<Enrollment> createEnrollment(Course course, User student) {
        Enrollment enrollment = new Enrollment();
        enrollment.setCourseId(course.getId());
        enrollment.setStudentId(student.getId());
        enrollment.setEnrolledAt(LocalDateTime.now());
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollment.setProgress(0.0);
        
        return enrollmentRepository.save(enrollment)
                .doOnSuccess(savedEnrollment -> {
                    // Publish enrollment event
                    StudentEnrolledEvent event = new StudentEnrolledEvent(
                        savedEnrollment.getId(),
                        course.getId(),
                        student.getId(),
                        course.getTitle()
                    );
                    rabbitTemplate.convertAndSend("enrollment.events", "student.enrolled", event);
                });
    }
    
    public Mono<Enrollment> updateProgress(Long enrollmentId, Double progress, String studentId) {
        return enrollmentRepository.findById(enrollmentId)
                .filter(enrollment -> enrollment.getStudentId().equals(studentId))
                .switchIfEmpty(Mono.error(new EnrollmentNotFoundException("Enrollment not found")))
                .flatMap(enrollment -> {
                    enrollment.setProgress(progress);
                    enrollment.setLastActivityAt(LocalDateTime.now());
                    
                    if (progress >= 100.0) {
                        enrollment.setStatus(EnrollmentStatus.COMPLETED);
                        enrollment.setCompletedAt(LocalDateTime.now());
                        
                        // Generate certificate
                        return certificateService.generateCertificate(enrollment)
                                .then(enrollmentRepository.save(enrollment))
                                .doOnSuccess(completedEnrollment -> {
                                    // Publish completion event
                                    CourseCompletedEvent event = new CourseCompletedEvent(
                                        completedEnrollment.getId(),
                                        completedEnrollment.getCourseId(),
                                        completedEnrollment.getStudentId()
                                    );
                                    rabbitTemplate.convertAndSend("enrollment.events", "course.completed", event);
                                });
                    }
                    
                    return enrollmentRepository.save(enrollment);
                });
    }
    
    public Flux<Enrollment> getStudentEnrollments(String studentId) {
        return enrollmentRepository.findByStudentId(studentId)
                .flatMap(enrollment -> 
                    courseRepository.findById(enrollment.getCourseId())
                            .map(course -> {
                                enrollment.setCourse(course);
                                return enrollment;
                            })
                );
    }
    
    public Mono<EnrollmentAnalytics> getEnrollmentAnalytics(String studentId) {
        return enrollmentRepository.findByStudentId(studentId)
                .collectList()
                .map(enrollments -> {
                    long totalEnrollments = enrollments.size();
                    long completedCourses = enrollments.stream()
                            .filter(e -> e.getStatus() == EnrollmentStatus.COMPLETED)
                            .count();
                    double averageProgress = enrollments.stream()
                            .mapToDouble(Enrollment::getProgress)
                            .average()
                            .orElse(0.0);
                    
                    return new EnrollmentAnalytics(totalEnrollments, completedCourses, averageProgress);
                });
    }
}
```

#### 2.7 Progress Tracking with Lesson Completion

```java
@Entity
@Table(name = "lesson_completions")
public class LessonCompletion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long enrollmentId;
    
    @Column(nullable = false)
    private Long moduleId;
    
    @Column(nullable = false)
    private Long lessonId;
    
    @Column(nullable = false)
    private LocalDateTime completedAt;
    
    @Column(nullable = false)
    private Integer timeSpent; // in seconds
    
    @Column(nullable = false)
    private Double score; // percentage
    
    // Getters, setters, constructors
}

@Service
public class ProgressTrackingService {
    
    private final LessonCompletionRepository lessonCompletionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final RabbitTemplate rabbitTemplate;
    
    public Mono<LessonCompletion> completeLesson(CompleteLessonRequest request, String studentId) {
        return enrollmentRepository.findByCourseIdAndStudentId(request.getCourseId(), studentId)
                .switchIfEmpty(Mono.error(new EnrollmentNotFoundException("Student not enrolled")))
                .flatMap(enrollment -> {
                    LessonCompletion completion = new LessonCompletion();
                    completion.setEnrollmentId(enrollment.getId());
                    completion.setModuleId(request.getModuleId());
                    completion.setLessonId(request.getLessonId());
                    completion.setCompletedAt(LocalDateTime.now());
                    completion.setTimeSpent(request.getTimeSpent());
                    completion.setScore(request.getScore());
                    
                    return lessonCompletionRepository.save(completion)
                            .doOnSuccess(savedCompletion -> {
                                // Calculate and update overall progress
                                updateEnrollmentProgress(enrollment.getId());
                                
                                // Publish lesson completed event
                                LessonCompletedEvent event = new LessonCompletedEvent(
                                    savedCompletion.getId(),
                                    enrollment.getId(),
                                    request.getModuleId(),
                                    request.getLessonId(),
                                    request.getScore()
                                );
                                rabbitTemplate.convertAndSend("progress.events", "lesson.completed", event);
                            });
                });
    }
    
    private Mono<Void> updateEnrollmentProgress(Long enrollmentId) {
        return lessonCompletionRepository.findByEnrollmentId(enrollmentId)
                .collectList()
                .flatMap(completions -> {
                    // Calculate progress based on completed lessons vs total lessons
                    double progress = calculateProgress(completions);
                    
                    return enrollmentRepository.findById(enrollmentId)
                            .flatMap(enrollment -> {
                                enrollment.setProgress(progress);
                                enrollment.setLastActivityAt(LocalDateTime.now());
                                return enrollmentRepository.save(enrollment);
                            })
                            .then();
                });
    }
    
    private double calculateProgress(List<LessonCompletion> completions) {
        // Implementation depends on course structure
        // This is a simplified calculation
        return completions.size() * 10.0; // Assuming 10 lessons per course
    }
}
```

### Task 3: Real-time Communication

#### 2.8 WebSocket Configuration

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*")
                .withSockJS();
    }
    
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(64 * 1024) // 64KB
                .setSendBufferSizeLimit(512 * 1024) // 512KB
                .setSendTimeLimit(20000); // 20 seconds
    }
}
```

#### 2.9 Real-time Chat Implementation

```java
@Controller
public class ChatController {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final JwtTokenProvider jwtTokenProvider;
    
    @MessageMapping("/chat/{courseId}")
    @SendTo("/topic/chat/{courseId}")
    public ChatMessage handleChatMessage(@Payload ChatMessageRequest request,
                                       @Header("Authorization") String token,
                                       @DestinationVariable Long courseId) {
        
        String userId = jwtTokenProvider.getUserIdFromToken(token);
        String username = jwtTokenProvider.getUsernameFromToken(token);
        
        ChatMessage message = new ChatMessage();
        message.setCourseId(courseId);
        message.setUserId(userId);
        message.setUsername(username);
        message.setContent(request.getContent());
        message.setTimestamp(LocalDateTime.now());
        message.setMessageType(request.getMessageType());
        
        // Save message to database
        chatService.saveMessage(message);
        
        return message;
    }
    
    @MessageMapping("/chat/{courseId}/typing")
    @SendTo("/topic/chat/{courseId}/typing")
    public TypingNotification handleTyping(@Payload TypingRequest request,
                                         @Header("Authorization") String token,
                                         @DestinationVariable Long courseId) {
        
        String userId = jwtTokenProvider.getUserIdFromToken(token);
        String username = jwtTokenProvider.getUsernameFromToken(token);
        
        return new TypingNotification(userId, username, request.isTyping());
    }
    
    @EventListener
    public void handleUserConnected(SessionConnectedEvent event) {
        // Handle user connection
        String userId = extractUserIdFromSession(event.getUser());
        chatService.userConnected(userId);
    }
    
    @EventListener
    public void handleUserDisconnected(SessionDisconnectEvent event) {
        // Handle user disconnection
        String userId = extractUserIdFromSession(event.getUser());
        chatService.userDisconnected(userId);
    }
}
```

#### 2.10 Live Quiz Implementation

```java
@Controller
public class LiveQuizController {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final QuizService quizService;
    private final RedisTemplate<String, Object> redisTemplate;
    
    @MessageMapping("/quiz/{quizId}/start")
    @SendTo("/topic/quiz/{quizId}")
    public QuizStartEvent startQuiz(@Payload StartQuizRequest request,
                                   @DestinationVariable Long quizId) {
        
        Quiz quiz = quizService.getQuizById(quizId);
        QuizStartEvent event = new QuizStartEvent(quizId, quiz.getQuestions(), quiz.getDuration());
        
        // Store quiz state in Redis
        redisTemplate.opsForValue().set("quiz:" + quizId + ":state", "ACTIVE");
        redisTemplate.expire("quiz:" + quizId + ":state", Duration.ofMinutes(quiz.getDuration()));
        
        return event;
    }
    
    @MessageMapping("/quiz/{quizId}/submit")
    public void submitQuizAnswer(@Payload QuizAnswerRequest request,
                                @Header("Authorization") String token,
                                @DestinationVariable Long quizId) {
        
        String userId = jwtTokenProvider.getUserIdFromToken(token);
        
        // Save answer
        QuizAnswer answer = new QuizAnswer();
        answer.setQuizId(quizId);
        answer.setUserId(userId);
        answer.setQuestionId(request.getQuestionId());
        answer.setAnswer(request.getAnswer());
        answer.setSubmittedAt(LocalDateTime.now());
        
        quizService.saveAnswer(answer);
        
        // Send real-time update to instructor
        messagingTemplate.convertAndSend("/topic/quiz/" + quizId + "/answers", answer);
    }
    
    @MessageMapping("/quiz/{quizId}/end")
    @SendTo("/topic/quiz/{quizId}/results")
    public QuizResultsEvent endQuiz(@Payload EndQuizRequest request,
                                   @DestinationVariable Long quizId) {
        
        // Calculate results
        List<QuizAnswer> answers = quizService.getAnswersByQuizId(quizId);
        QuizResults results = quizService.calculateResults(quizId, answers);
        
        // Update Redis state
        redisTemplate.opsForValue().set("quiz:" + quizId + ":state", "COMPLETED");
        
        return new QuizResultsEvent(quizId, results);
    }
}
```

## Phase 3: Integration and Monitoring

### 3.1 API Gateway Configuration

```yaml
spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/api/v1/users/**
          filters:
            - name: CircuitBreaker
              args:
                name: user-service-circuit-breaker
                fallbackUri: forward:/fallback/user-service
        
        - id: course-service
          uri: lb://course-service
          predicates:
            - Path=/api/v1/courses/**
          filters:
            - name: CircuitBreaker
              args:
                name: course-service-circuit-breaker
                fallbackUri: forward:/fallback/course-service
        
        - id: enrollment-service
          uri: lb://enrollment-service
          predicates:
            - Path=/api/v1/enrollments/**
          filters:
            - name: CircuitBreaker
              args:
                name: enrollment-service-circuit-breaker
                fallbackUri: forward:/fallback/enrollment-service
        
        - id: assessment-service
          uri: lb://assessment-service
          predicates:
            - Path=/api/v1/assessments/**
          filters:
            - name: CircuitBreaker
              args:
                name: assessment-service-circuit-breaker
                fallbackUri: forward:/fallback/assessment-service
        
        - id: notification-service
          uri: lb://notification-service
          predicates:
            - Path=/api/v1/notifications/**
          filters:
            - name: CircuitBreaker
              args:
                name: notification-service-circuit-breaker
                fallbackUri: forward:/fallback/notification-service

resilience4j:
  circuitbreaker:
    instances:
      user-service-circuit-breaker:
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        failureRateThreshold: 50
        waitDurationInOpenState: 60s
      course-service-circuit-breaker:
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        failureRateThreshold: 50
        waitDurationInOpenState: 60s
      enrollment-service-circuit-breaker:
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        failureRateThreshold: 50
        waitDurationInOpenState: 60s
      assessment-service-circuit-breaker:
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        failureRateThreshold: 50
        waitDurationInOpenState: 60s
      notification-service-circuit-breaker:
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        failureRateThreshold: 50
        waitDurationInOpenState: 60s
```

### 3.2 Distributed Tracing Configuration

```java
@Configuration
public class TracingConfig {
    
    @Bean
    public Sampler defaultSampler() {
        return Sampler.ALWAYS_SAMPLE;
    }
    
    @Bean
    public SpanCustomizer spanCustomizer(Tracer tracer) {
        return tracer.currentSpanCustomizer();
    }
}
```

### 3.3 Monitoring with Micrometer

```java
@Configuration
public class MetricsConfig {
    
    @Bean
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }
    
    @Bean
    public TimedAspect timedAspect(MeterRegistry meterRegistry) {
        return new TimedAspect(meterRegistry);
    }
    
    @Bean
    public CountedAspect countedAspect(MeterRegistry meterRegistry) {
        return new CountedAspect(meterRegistry);
    }
}

@Component
public class CourseMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Counter courseCreatedCounter;
    private final Counter coursePublishedCounter;
    private final Timer courseSearchTimer;
    
    public CourseMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.courseCreatedCounter = Counter.builder("courses.created")
                .description("Number of courses created")
                .register(meterRegistry);
        this.coursePublishedCounter = Counter.builder("courses.published")
                .description("Number of courses published")
                .register(meterRegistry);
        this.courseSearchTimer = Timer.builder("courses.search.duration")
                .description("Time taken for course search")
                .register(meterRegistry);
    }
    
    public void incrementCourseCreated() {
        courseCreatedCounter.increment();
    }
    
    public void incrementCoursePublished() {
        coursePublishedCounter.increment();
    }
    
    public Timer.Sample startSearchTimer() {
        return Timer.start(meterRegistry);
    }
    
    public void stopSearchTimer(Timer.Sample sample) {
        sample.stop(courseSearchTimer);
    }
}
```

### 3.4 Docker Configuration

```dockerfile
# Multi-stage build for optimized image
FROM openjdk:17-jdk-slim as builder

WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN ./mvnw clean package -DskipTests

FROM openjdk:17-jre-slim

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 3.5 Kubernetes Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: course-service
  labels:
    app: course-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: course-service
  template:
    metadata:
      labels:
        app: course-service
    spec:
      containers:
      - name: course-service
        image: lms/course-service:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "kubernetes"
        - name: SPRING_DATASOURCE_URL
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: url
        - name: SPRING_DATASOURCE_USERNAME
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: username
        - name: SPRING_DATASOURCE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: password
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: course-service
spec:
  selector:
    app: course-service
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: ClusterIP
```

## Testing Strategy

### 3.6 Unit Testing

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
    void createCourse_Success() {
        // Given
        CreateCourseRequest request = new CreateCourseRequest("Test Course", "Description");
        String instructorId = "instructor123";
        Course savedCourse = new Course();
        savedCourse.setId(1L);
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
        
        verify(rabbitTemplate).convertAndSend(eq("course.events"), eq("course.created"), any());
    }
}
```

### 3.7 Integration Testing

```java
@SpringBootTest
@Testcontainers
class CourseControllerIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @Container
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3-management")
            .withUser("guest", "guest");
    
    @Autowired
    private WebTestClient webTestClient;
    
    @Test
    void createCourse_ValidRequest_ReturnsCreatedCourse() {
        CreateCourseRequest request = new CreateCourseRequest("Test Course", "Description");
        
        webTestClient.post()
                .uri("/api/v1/courses")
                .header("Authorization", "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Test Course")
                .jsonPath("$.description").isEqualTo("Description");
    }
}
```

## Performance Optimization

### 3.8 Caching Strategy

```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }
    
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfiguration())
                .withCacheConfiguration("courses", 
                    cacheConfiguration().entryTtl(Duration.ofMinutes(15)))
                .withCacheConfiguration("users", 
                    cacheConfiguration().entryTtl(Duration.ofMinutes(60)))
                .build();
    }
}

@Service
public class CourseService {
    
    @Cacheable(value = "courses", key = "#courseId")
    public Mono<Course> getCourseById(Long courseId) {
        return courseRepository.findById(courseId);
    }
    
    @CacheEvict(value = "courses", key = "#courseId")
    public Mono<Course> updateCourse(Long courseId, UpdateCourseRequest request, String instructorId) {
        // Implementation
    }
}
```

This implementation provides a comprehensive foundation for the AI-Powered Learning Management System backend, covering all the required phases and features with modern Spring Boot 3, reactive programming, and cloud-native patterns. 