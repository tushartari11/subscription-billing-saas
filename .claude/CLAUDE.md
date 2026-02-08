# Subscription Billing SaaS - Project Instructions

## Project Overview
This is a Spring Boot 4.0.2 application for subscription and billing management (SaaS platform).

### Tech Stack
- **Framework**: Spring Boot 4.0.2
- **Language**: Java 21
- **Database**: PostgreSQL
- **Security**: Spring Security + JWT (v0.12.6)
- **API Documentation**: SpringDoc OpenAPI (Swagger) v2.8.4
- **Utilities**: Lombok
- **Database Migration**: Flyway
- **Build Tool**: Maven

## Architecture

### Package Structure
```
com.rekreation.saas.subscriptionmanagement/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── dto/            # Data Transfer Objects
├── entity/         # JPA entities
├── exception/      # Custom exceptions
├── repository/     # JPA repositories
├── service/        # Business logic
└── util/           # Utility classes
```

## Development Guidelines

### Code Style
- Follow Google Java Style Guide (enforced via Checkstyle)
- Use Lombok for boilerplate reduction
- Keep classes focused and single-purpose
- Use meaningful variable and method names

### Entity Design
- Use JPA annotations properly
- Include audit fields (createdAt, updatedAt)
- Use appropriate relationships (@OneToMany, @ManyToOne, etc.)
- Implement soft deletes where appropriate

### API Design
- Follow RESTful principles
- Use proper HTTP methods (GET, POST, PUT, DELETE)
- Return appropriate status codes
- Include proper error handling
- Document with OpenAPI annotations

### Security
- All endpoints should be secured by default
- Use JWT for authentication
- Implement role-based access control (RBAC)
- Validate all inputs
- Sanitize outputs to prevent XSS

### Database
- Use Flyway for migrations
- Never modify existing migrations
- Use versioned migrations (V1__description.sql)
- Include rollback strategies

### Testing
- Write unit tests for services
- Write integration tests for controllers
- Use @SpringBootTest for integration tests
- Mock external dependencies

## Environment Configuration

Create a `.env` file with:
```
# Database
DB_URL=jdbc:postgresql://localhost:5432/subscription_db
DB_USERNAME=your_username
DB_PASSWORD=your_password

# JWT
JWT_SECRET=your-secret-key-here
JWT_EXPIRATION=86400000

# API Keys
NVD_API_KEY=your-nvd-api-key
```

## Build & Run

### Build
```bash
./mvnw clean install
```

### Run
```bash
./mvnw spring-boot:run
```

### Run Tests
```bash
./mvnw test
```

## API Documentation
Once running, access Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

## Code Quality

### Checkstyle
```bash
./mvnw checkstyle:check
```

### SpotBugs
```bash
./mvnw spotbugs:check
```

### Security Scan
```bash
./mvnw dependency-check:check
```

## Important Notes
- Never commit secrets or API keys
- Use `.env` for local configuration
- Follow the existing code patterns
- Write self-documenting code
- Keep security in mind for all changes
