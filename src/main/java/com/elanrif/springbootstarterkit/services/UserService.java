package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto.Response createUser(UserDto.CreateRequest request);

    UserDto.Response update(Long id, UserDto.UpdateRequest request);

    List<UserDto.Response> getAll();

    UserDto.Response getById(Long id);

    void deleteUser(Long id);

    List<UserDto.Response> searchUsers(String email, String firstName, String lastName, Boolean isActive);
}
