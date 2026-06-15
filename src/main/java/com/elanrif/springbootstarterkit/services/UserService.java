package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.UserDto;
import com.elanrif.springbootstarterkit.entity.UserRole;
import com.elanrif.springbootstarterkit.entity.UserStatus;
import com.elanrif.springbootstarterkit.util.PageResponse;

public interface UserService {

    UserDto.Response createUser(UserDto.CreateRequest request);

    UserDto.Response update(Long id, UserDto.UpdateRequest request);

    PageResponse<UserDto.Response> getAll(int page, int size, UserRole role, UserStatus status, String sort);

    UserDto.Response getById(Long id);

    void deleteUser(Long id);

    PageResponse<UserDto.Response> searchUsers(String email, String firstName, String lastName, UserStatus status, int page, int size, String sort);
}
