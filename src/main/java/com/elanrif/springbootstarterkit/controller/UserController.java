package com.elanrif.springbootstarterkit.controller;

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

    @PatchMapping("/{id}")
    public UserDto updateMe(@PathVariable Long id, @Valid @RequestBody UserUpdateDto dto) {
        return userService.updateMe(id, dto);
    }

    @GetMapping
    public List<UserDto> list() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        return userService.getById(id);
    }

}

