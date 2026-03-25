# Kickstart Spring Boot Template

## Prerequisites
- Java 21
- Maven
- PostgreSQL (if used locally)

## MapStruct & Lombok
To enable MapStruct to generate mappers using Lombok-generated getters/setters, a specific configuration of the `maven-compiler-plugin` is required.

### Useful Documentation
- [MapStruct + Lombok on Baeldung](https://www.baeldung.com/java-mapstruct-lombok)

### MapStruct Mapper Example
```java
@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toEntity(ProductDTO dto);
    ProductDTO toDto(Product entity);
}
```

### Maven Dependencies for MapStruct
```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>${org.mapstruct.version}</version>
</dependency>
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct-processor</artifactId>
    <version>${org.mapstruct.version}</version>
    <scope>provided</scope>
</dependency>
```

### Compiler Configuration
Your `pom.xml` should include `lombok-mapstruct-binding` in the `annotationProcessorPaths`. This allows MapStruct to "see" Lombok-generated methods during compilation.

```xml
<annotationProcessorPaths>
    <path>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>${lombok.version}</version>
    </path>
    <path>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok-mapstruct-binding</artifactId>
        <version>0.2.0</version>
    </path>
    <path>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct-processor</artifactId>
        <version>${org.mapstruct.version}</version>
    </path>
</annotationProcessorPaths>
```
