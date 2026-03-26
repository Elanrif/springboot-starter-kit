## Error Handling - RFC 9457 (Problem Details for HTTP APIs)

This project uses the **RFC 9457** standard for error responses, natively supported by Spring Boot 3+.

> **Note:** RFC 9457 obsoletes RFC 7807 and is the current IETF standard (July 2023).

### What is RFC 9457?

RFC 9457 is an **IETF standard** that defines a uniform format for returning errors in HTTP APIs. Instead of each API inventing its own error format, this standard proposes a standardized JSON (or XML) format with the media type `application/problem+json`.

### Structure du format

```json
{
  "type": "https://example.com/probs/out-of-credit",
  "title": "You do not have enough credit.",
  "status": 403,
  "detail": "Your current balance is 30, but that costs 50.",
  "instance": "/account/12345/msgs/abc"
}
```

| Field | Description |
|-------|-------------|
| `type` | URI identifying the problem type |
| `title` | Short, human-readable summary (doesn't change between occurrences) |
| `status` | HTTP status code (403, 404, 500...) |
| `detail` | Human-readable explanation specific to this occurrence |
| `instance` | URI identifying this specific occurrence |

### Advantages

- **Interoperability** - All clients can parse the same format
- **Extensible** - Custom fields can be added (`balance`, `errors`...)
- **Machine-readable** - Clients can automatically react to errors

### Project Error Response Format

All API errors in this project return the standardized format:

```json
{
  "type": "about:blank",
  "title": "Resource not found",
  "status": 404,
  "detail": "User not found: 99",
  "instance": "/api/users/99",
  "errorCode": "RESOURCE_NOT_FOUND"
}
```

### Error Codes

| Error Code | HTTP Status | Description |
|------------|-------------|-------------|
| `RESOURCE_NOT_FOUND` | 404 | Resource not found |
| `BAD_REQUEST` | 400 | Invalid request |
| `VALIDATION_ERROR` | 400 | Validation failed |
| `INTERNAL_ERROR` | 500 | Unexpected server error |

### Validation Error Example

```json
{
  "type": "about:blank",
  "title": "Validation error",
  "status": 400,
  "detail": "Validation failed",
  "instance": "/api/users",
  "errorCode": "VALIDATION_ERROR",
  "errors": [
    "email : Invalid email format",
    "name : must not be blank"
  ]
}
```

### Usage in Services

```java
// Throw ResourceNotFoundException
throw new ResourceNotFoundException("User not found: " + id);

// Throw BadRequestException
throw new BadRequestException("Invalid password");
```

### Spring Boot Integration

Spring natively supports this standard via `ProblemDetail`:

```java
@ExceptionHandler(ResourceNotFoundException.class)
public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
        HttpStatus.NOT_FOUND,
        ex.getMessage()
    );
    problem.setTitle("Resource Not Found");
    problem.setType(URI.create("https://api.example.com/errors/not-found"));
    return problem;
}
```

Or enable globally in `application.properties`:

```properties
spring.mvc.problemdetails.enabled=true
```

### References

- [RFC 9457 - Problem Details for HTTP APIs](https://datatracker.ietf.org/doc/html/rfc9457)
- [RFC 7807 - Problem Details for HTTP APIs (obsoleted)](https://datatracker.ietf.org/doc/html/rfc7807)
- [Spring Boot ProblemDetail](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/ProblemDetail.html)
