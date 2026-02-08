# Coding Guidelines - Subscription Billing SaaS

## General Principles

### 1. Code Organization
- One class per file
- Package by feature, not by layer
- Keep related code together
- Minimize cross-package dependencies

### 2. Naming Conventions
- **Classes**: PascalCase (e.g., `UserService`, `SubscriptionController`)
- **Methods**: camelCase (e.g., `getUserById`, `createSubscription`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_RETRY_ATTEMPTS`)
- **Variables**: camelCase (e.g., `userName`, `subscriptionId`)

### 3. Method Design
- Keep methods small (< 20 lines preferred)
- One responsibility per method
- Use descriptive names
- Avoid side effects
- Return early to reduce nesting

### 4. Error Handling
```java
// Good
public User findUserById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));
}

// Bad
public User findUserById(Long id) {
    try {
        return userRepository.findById(id).get();
    } catch (Exception e) {
        return null;
    }
}
```

## Spring Boot Specific

### 1. Controller Guidelines
```java
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserDTO user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}
```

### 2. Service Layer
```java
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
        return UserMapper.toDTO(user);
    }

    public UserDTO createUser(CreateUserRequest request) {
        // Validation
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        // Business logic
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        return UserMapper.toDTO(savedUser);
    }
}
```

### 3. Entity Design
```java
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Boolean active = true;
}
```

### 4. DTO Usage
```java
@Data
@Builder
public class UserDTO {
    private Long id;
    private String email;
    private String role;
    private LocalDateTime createdAt;
    // No password in DTO!
}

@Data
public class CreateUserRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
```

## Security Guidelines

### 1. Input Validation
- Always validate user input
- Use `@Valid` on request bodies
- Validate at controller level
- Use custom validators for complex rules

### 2. Password Handling
- Never store plain text passwords
- Use BCrypt for password encoding
- Enforce strong password policies
- Never log passwords

### 3. SQL Injection Prevention
- Use parameterized queries (JPA does this)
- Avoid string concatenation in queries
- Validate input types

### 4. Authentication & Authorization
```java
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long id) {
    // Only admins can delete users
}

@PreAuthorize("hasRole('USER') and #id == authentication.principal.id")
public UserDTO updateUser(Long id, UpdateUserRequest request) {
    // Users can only update their own profile
}
```

## Database Guidelines

### 1. Flyway Migrations
```sql
-- V1__create_users_table.sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
```

### 2. Repository Patterns
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.active = true AND u.role = :role")
    List<User> findActiveUsersByRole(@Param("role") UserRole role);
}
```

## Testing Guidelines

### 1. Unit Tests
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserById_WhenUserExists_ReturnsUser() {
        // Given
        Long userId = 1L;
        User user = User.builder().id(userId).email("test@test.com").build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        UserDTO result = userService.getUserById(userId);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("test@test.com", result.getEmail());
    }
}
```

### 2. Integration Tests
```java
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createUser_WithValidData_ReturnsCreated() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "test@test.com",
                        "password": "password123"
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }
}
```

## Code Review Checklist

- [ ] Code follows naming conventions
- [ ] Methods are small and focused
- [ ] Error handling is proper
- [ ] Input validation is present
- [ ] Security considerations addressed
- [ ] Tests are written
- [ ] No hardcoded values
- [ ] Logging is appropriate
- [ ] Documentation is clear
- [ ] No code duplication
