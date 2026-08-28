package com.elanrif.springbootstarterkit.controller;

import com.elanrif.springbootstarterkit.dto.AddressDto;
import com.elanrif.springbootstarterkit.dto.CommonDto;
import com.elanrif.springbootstarterkit.dto.UserDto;
import com.elanrif.springbootstarterkit.services.AddressService;
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
    private final AddressService addressService;

    // =========================================================
    // RCUD (Read,Create,Update,Delete) operations for User entity
    // =========================================================

    @GetMapping
    public ResponseEntity<PageResponse<UserDto.Response>> getUsers(
            @ModelAttribute UserDto.Filter filter,
            @ModelAttribute CommonDto.Pagination pagination
    ) {
        log.info(
                "GET /api/v1/users - page: {}, size: {}",
                pagination.page(),
                pagination.size()
        );

        PageResponse<UserDto.Response> response =
                userService.getUsers(filter, pagination);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto.Response> getUser(
            @PathVariable Long id
    ) {
        log.info("GET /api/v1/users/{} - Fetching user", id);

        UserDto.Response response =
                userService.getById(id);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<UserDto.Response> createUser(
            @Valid @RequestBody UserDto.CreateRequest request
    ) {
        log.info(
                "POST /api/v1/users - Creating user with email: {}",
                request.email()
        );

        UserDto.Response response =
                userService.createUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto.Response> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDto.UpdateRequest request
    ) {
        log.info("PATCH /api/v1/users/{} - Updating user", id);

        UserDto.Response response =
                userService.updateUser(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id
    ) {
        log.info("DELETE /api/v1/users/{} - Deleting user", id);

        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // Address operations for User
    // =========================================================

    @GetMapping("/{userId}/addresses")
    public ResponseEntity<PageResponse<AddressDto.Response>> getUserAddresses(
            @PathVariable Long userId,
            @ModelAttribute AddressDto.Filter filter,
            @ModelAttribute CommonDto.Pagination pagination
    ) {
        log.info(
                "GET /api/v1/users/{}/addresses - Fetching addresses",
                userId
        );

        PageResponse<AddressDto.Response> response =
                addressService.getAddressesByUserId(
                        userId,
                        filter,
                        pagination
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{userId}/addresses")
    public ResponseEntity<AddressDto.Response> createUserAddress(
            @PathVariable Long userId,
            @Valid @RequestBody AddressDto.CreateRequest request
    ) {
        log.info(
                "POST /api/v1/users/{}/addresses - Creating address",
                userId
        );
        AddressDto.Response response =
                addressService.createUserAddress(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}