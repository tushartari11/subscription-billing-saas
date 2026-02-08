# Code Reviewer Agent

## Purpose
Automated code review for quality, best practices, security, and maintainability.

## What It Checks

### 1. Code Quality
- Naming conventions
- Method complexity
- Code duplication
- Single Responsibility Principle
- DRY (Don't Repeat Yourself)

### 2. Spring Boot Best Practices
- Proper use of annotations
- Dependency injection patterns
- Transaction management
- Exception handling
- REST API design

### 3. Security Issues
- Input validation
- SQL injection risks
- XSS vulnerabilities
- Authentication/Authorization
- Sensitive data exposure
- Password handling

### 4. Performance
- N+1 query problems
- Missing database indexes
- Unnecessary object creation
- Proper caching usage

### 5. Testing
- Test coverage
- Test quality
- Mock usage
- Integration test completeness

## How to Use

Simply ask: "Review the UserService class" or "Review my recent changes"

## Review Output Format

### Summary
- Overall code quality score
- Number of issues found
- Critical issues count

### Issues by Category
1. **Critical** - Must fix
2. **High** - Should fix soon
3. **Medium** - Consider fixing
4. **Low** - Nice to have

### Example Review

```
Code Review: UserService.java

Score: 7/10

Issues Found: 5

Critical Issues (1):
- Line 45: SQL injection vulnerability in findByCustomQuery()
  Fix: Use parameterized query instead of string concatenation

High Issues (2):
- Line 23: Missing input validation for email
  Fix: Add @Valid annotation and email validation
- Line 67: Password logged in plain text
  Fix: Remove password from log statement

Medium Issues (2):
- Line 15: Method too complex (cognitive complexity: 15)
  Fix: Extract helper methods
- Line 89: No transaction boundary
  Fix: Add @Transactional annotation

Suggestions:
- Consider adding caching for frequently accessed users
- Add integration tests for user creation flow
```

## Integration
This agent follows the coding guidelines in `coding-guidelines.md`
