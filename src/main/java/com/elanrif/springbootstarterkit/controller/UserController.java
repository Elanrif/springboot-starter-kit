package com.elanrif.springbootstarterkit.controller;

import com.elanrif.springbootstarterkit.dto.UserDto;
import com.elanrif.springbootstarterkit.services.UserService;
import com.elanrif.springbootstarterkit.util.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto.Response> createUser(@Valid @RequestBody UserDto.CreateRequest request) {
        log.info("POST /api/v1/users - Creating user with email: {}", request.email());
        UserDto.Response response = userService.createUser(request);
        log.info("User created with id: {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto.Response> updateMe(@PathVariable Long id, @Valid @RequestBody UserDto.UpdateRequest request) {
        log.info("PATCH /api/v1/users/{} - Updating user", id);
        UserDto.Response response = userService.update(id, request);
        log.info("User updated with id: {}", id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<UserDto.Response>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        log.info("GET /api/v1/users - Fetching users page: {}, size: {}", page, size);
        PageResponse<UserDto.Response> response = userService.getAll(page, size, sort);
        log.info("Returned {} users (total: {})", response.data().size(), response.meta().total());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto.Response> getById(@PathVariable Long id) {
        log.info("GET /api/v1/users/{} - Fetching user by id", id);
        UserDto.Response response = userService.getById(id);
        log.info("Returned user with id: {}", id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("DELETE /api/v1/users/{} - Deleting user", id);
        userService.deleteUser(id);
        log.info("User deleted with id: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<UserDto.Response>> searchUsers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        log.info("GET /api/v1/users/search - Searching users with email: {}, firstName: {}, lastName: {}, isActive: {}",
                email, firstName, lastName, isActive);
        PageResponse<UserDto.Response> response = userService.searchUsers(email, firstName, lastName, isActive, page, size, sort);
        log.info("Search returned {} users (total: {})", response.data().size(), response.meta().total());
        return ResponseEntity.ok(response);
    }
}
