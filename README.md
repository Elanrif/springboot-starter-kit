# Spring Boot Starter Kit

A starter kit for building Spring Boot applications with essential dependencies pre-configured.

## Requirements

- **Java Version:** 21

## Installed Dependencies

The following dependencies are installed in this project:

| Dependency                                                                              | Category | Description |
|-----------------------------------------------------------------------------------------|----------|-------------|
| Spring Boot DevTools                                                                    | Developer Tools | Provides fast application restarts, LiveReload, and configurations for enhanced development experience. |
| Lombok                                                                                  | Developer Tools | Java annotation library which helps to reduce boilerplate code. |
| Spring Web                                                                              | Web | Build web, including RESTful, applications using Spring MVC. Uses Apache Tomcat as the default embedded container. |
| Spring Security                                                                         | Security | Highly customizable authentication and access-control framework for Spring applications. |
| OAuth2 Resource Server                                                                  | Security | Spring Boot integration for Spring Security's OAuth2 resource server features. |
| PostgreSQL Driver                                                                       | SQL | A JDBC and R2DBC driver that allows Java programs to connect to a PostgreSQL database using standard, database independent Java code. |
| Spring Data JPA                                                                         | SQL | Persist data in SQL stores with Java Persistence API using Spring Data and Hibernate. |
| Spring Validation                                                                       | Web | Bean Validation with Hibernate validator. |
| SpringDoc OpenAPI                                                                       | Documentation | OpenAPI 3 documentation for Spring Boot applications. |
| ⚠️ [MapStruct](https://mapstruct.org/documentation/stable/reference/html/#introduction) | Developer Tools | Code generator that simplifies mappings between Java bean types. |

![spring_initializr.png](spring_initializr.png)

## Error Handling - RFC 7807 (Problem Details)

This project uses the **RFC 7807** standard for error responses, natively supported by Spring Boot 3+.

### Error Response Format

All API errors return a standardized JSON format:

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

### References

- [RFC 7807 - Problem Details for HTTP APIs](https://datatracker.ietf.org/doc/html/rfc7807)
- [Spring Boot ProblemDetail](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/ProblemDetail.html)