package com.elanrif.springbootstarterkit.controller;

import com.elanrif.springbootstarterkit.dto.UserDto;
import com.elanrif.springbootstarterkit.entity.UserRole;
import com.elanrif.springbootstarterkit.entity.UserStatus;
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
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sort) {
        // 👈 Convertit la page reçue (commençant à 1) en index Spring (commençant à 0)
        // et protège contre les valeurs négatives ou égales à zéro.
        page = Math.max(0, page - 1);
        log.info("GET /api/v1/users - Fetching users page: {}, limit: {}", page, limit);

        // toUppercase role, status
        UserStatus userStatus = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                userStatus = UserStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status provided: {}, ignoring filter.", status);
            }
        }

        UserRole userRole = null;
        if (role != null && !role.trim().isEmpty()) {
            try {
                userRole = UserRole.valueOf(role.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid role provided: {}, ignoring filter.", role);
            }
        }

        PageResponse<UserDto.Response> response = userService.getAll(page, limit, userRole, userStatus, sort);
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
            @RequestParam(required = false) UserStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String sort) {
        log.info("GET /api/v1/users/search - Searching users with email: {}, firstName: {}, lastName: {}, status: {}",
                email, firstName, lastName, status);
        PageResponse<UserDto.Response> response = userService.searchUsers(email, firstName, lastName, status, page, limit, sort);
        log.info("Search returned {} users (total: {})", response.data().size(), response.meta().total());
        return ResponseEntity.ok(response);
    }
}
