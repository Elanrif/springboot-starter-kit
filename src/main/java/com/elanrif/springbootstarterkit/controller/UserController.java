package com.elanrif.springbootstarterkit.controller;

import com.elanrif.springbootstarterkit.dto.user.UserCreateDto;
import com.elanrif.springbootstarterkit.dto.user.UserDto;
import com.elanrif.springbootstarterkit.dto.user.UserUpdateDto;
import com.elanrif.springbootstarterkit.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserCreateDto dto) {
        return userService.createUser(dto);
    }

    @PatchMapping("/{id}")
    public UserDto updateMe(@PathVariable Long id, @Valid @RequestBody UserUpdateDto dto) {
        return userService.update(id, dto);
    }

    @GetMapping
    public List<UserDto> list() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/search")
    public List<UserDto> searchUsers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) Boolean isActive) {
        return userService.searchUsers(email,firstName, lastName, isActive);
    }
}
