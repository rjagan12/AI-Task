# User Management Microservice

A complete RESTful User Management API built with Java, Spring Boot, and PostgreSQL. This microservice supports user CRUD operations, JWT-based authentication, role management, input validation, error handling, and logging.

---

## Features
- **RESTful API**: CRUD operations for users
- **Authentication**: JWT-based login, refresh, and logout
- **Role Management**: Assign/remove roles to users
- **Input Validation**: DTO-level validation
- **Error Handling**: Custom exceptions and global error responses
- **Logging**: SLF4J logging throughout
- **Database**: PostgreSQL with connection pooling (HikariCP)
- **Testing**: Ready for unit and integration tests

---

## Architecture
- **Controller Layer**: Handles HTTP requests and responses
- **Service Layer**: Business logic and validation
- **Repository Layer**: JPA for database access
- **Security Layer**: JWT authentication, password hashing, and authorization
- **DTOs**: For request/response payloads
- **Exception Handling**: Custom exceptions for clear error messages

---

## Project Structure
```
src/main/java/com/usermanagement/
  ├── config/         # Spring Security and app config
  ├── controller/     # REST controllers
  ├── dto/            # Data Transfer Objects
  ├── entity/         # JPA entities
  ├── exception/      # Custom exceptions
  ├── repository/     # Spring Data JPA repositories
  ├── security/       # JWT and security classes
  ├── service/        # Service interfaces and implementations
  └── UserManagementApplication.java
src/main/resources/
  └── application.yml # Main configuration
```

---

## Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL 12+

---

## Setup & Run

1. **Clone the repository**
   ```bash
   git clone <your-repo-url>
   cd Week-2
   ```

2. **Configure PostgreSQL**
   - Create a database named `user_management_db`.
   - Update `src/main/resources/application.yml` with your DB username and password if needed.

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```
   The API will be available at: `http://localhost:8080/api/v1/`

---

## API Endpoints

### Authentication
- `POST /api/v1/auth/login` — User login (returns JWT)
- `POST /api/v1/auth/refresh` — Refresh JWT token
- `POST /api/v1/auth/logout` — Logout (token invalidation placeholder)

### User Management
- `POST /api/v1/users` — Create user
- `GET /api/v1/users/{id}` — Get user by ID
- `GET /api/v1/users/username/{username}` — Get user by username
- `GET /api/v1/users/email/{email}` — Get user by email
- `GET /api/v1/users` — List users (pagination)
- `PUT /api/v1/users/{id}` — Update user
- `DELETE /api/v1/users/{id}` — Delete user
- `PATCH /api/v1/users/{id}/deactivate` — Deactivate user
- `PATCH /api/v1/users/{id}/activate` — Activate user
- `POST /api/v1/users/{userId}/roles/{role}` — Add role to user
- `DELETE /api/v1/users/{userId}/roles/{role}` — Remove role from user
- `GET /api/v1/users/active` — List active users
- `GET /api/v1/users/inactive` — List inactive users
- `GET /api/v1/users/search/name?name=...` — Search users by name
- `GET /api/v1/users/search/email?email=...` — Search users by email
- `GET /api/v1/users/role/{role}` — List users by role
- `GET /api/v1/users/stats/active-count` — Count of active users
- `GET /api/v1/users/stats/inactive-count` — Count of inactive users
- `GET /api/v1/users/check/username/{username}` — Check if username exists
- `GET /api/v1/users/check/email/{email}` — Check if email exists

---

## Authentication & Usage
- All endpoints except `/auth/**` require a valid JWT in the `Authorization: Bearer <token>` header.
- Only users with the `ADMIN` role can create, update, or delete users and manage roles.

---

## Testing
- You can use Postman or curl to test the endpoints.
- To add unit/integration tests, place them in `src/test/java/com/usermanagement/`.

---

## Logging
- Logs are written to `logs/user-management-service.log` and the console.

---

## License
MIT (or specify your license) 