package com.elanrif.springbootstarterkit.controller;

import com.elanrif.springbootstarterkit.dto.PaginationDto;
import com.elanrif.springbootstarterkit.dto.UserDto;
import com.elanrif.springbootstarterkit.dto.validation.OnCreate;
import com.elanrif.springbootstarterkit.dto.validation.OnUpdate;
import com.elanrif.springbootstarterkit.services.AddressService;
import com.elanrif.springbootstarterkit.services.UserService;
import com.elanrif.springbootstarterkit.dto.shared.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users (Admin)", description = "Admin management of any user account")
public class UserAdminController {

    private final UserService userService;
    private final AddressService addressService;

    @GetMapping
    @Operation(
            summary = "List users",
            description = "Returns a paginated list of users. Admin only."
    )
    public ResponseEntity<PageResponse<UserDto.Response>> getUsers(
            @ModelAttribute UserDto.Filter filter,
            @ModelAttribute PaginationDto.Pagination pagination
    ) {
        log.debug("GET /api/v1/users - page: {}, size: {}", pagination.page(), pagination.size());
        PageResponse<UserDto.Response> response = userService.getUsers(filter, pagination);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a user by id",
            description = "Returns a single user. Admin only."
    )
    public ResponseEntity<UserDto.Response> getUser(@PathVariable Long id) {
        log.debug("GET /api/v1/users/{}", id);
        UserDto.Response response = userService.getById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(
            summary = "Create a user",
            description = "Creates a new user account. Admin only."
    )
    public ResponseEntity<UserDto.Response> createUser(
            @Validated(OnCreate.class) @RequestBody UserDto.Request request
    ) {
        UserDto.Response response = userService.createUser(request);
        log.info("POST /api/v1/users - User created with id: {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Update a user",
            description = "Partially updates any user's account. Admin only."
    )
    public ResponseEntity<UserDto.Response> updateUser(
            @PathVariable Long id,
            @Validated(OnUpdate.class) @RequestBody UserDto.Request request
    ) {
        UserDto.Response response = userService.updateUser(id, request);
        log.info("PATCH /api/v1/users/{} - User updated", id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a user",
            description = "Soft-deletes a user account. Admin only."
    )
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        log.info("DELETE /api/v1/users/{} - User deleted", id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/purge")
    @Operation(
            summary = "Permanently purge a user",
            description = "Hard-deletes a previously soft-deleted user, removing all data permanently. Irreversible. Admin only."
    )
    public ResponseEntity<Void> purgeUser(@PathVariable Long id) {
        userService.purgeUser(id);
        log.info("DELETE /api/v1/users/{}/purge - User permanently purged", id);
        return ResponseEntity.noContent().build();
    }
}