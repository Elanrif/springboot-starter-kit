package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.UserDto;
import com.elanrif.springbootstarterkit.util.PageResponse;

public interface UserService {

    UserDto.Response createUser(UserDto.CreateRequest request);

    UserDto.Response update(Long id, UserDto.UpdateRequest request);

    PageResponse<UserDto.Response> getAll(int page, int size, String sort);

    UserDto.Response getById(Long id);

    void deleteUser(Long id);

    PageResponse<UserDto.Response> searchUsers(String email, String firstName, String lastName, Boolean isActive, int page, int size, String sort);
}
