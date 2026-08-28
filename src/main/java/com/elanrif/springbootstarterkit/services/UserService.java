package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.AddressDto;
import com.elanrif.springbootstarterkit.dto.CommonDto;
import com.elanrif.springbootstarterkit.dto.UserDto;
import com.elanrif.springbootstarterkit.entity.UserRole;
import com.elanrif.springbootstarterkit.entity.UserStatus;
import com.elanrif.springbootstarterkit.util.PageResponse;

public interface UserService {
    // =========================================================
    // CRUD operations for User entity
    // =========================================================
    UserDto.Response createUser(UserDto.CreateRequest request);
    PageResponse<UserDto.Response> getUsers(
            UserDto.Filter filter,
            CommonDto.Pagination pagination
    );
    UserDto.Response getById(Long id);
    UserDto.AddressesResponse getAddresses(
            Long userId,
            AddressDto.Filter filter,
            CommonDto.Pagination pagination
    );
    UserDto.Response updateUser(
            Long id,
            UserDto.UpdateRequest request
    );
    void deleteUser(Long id);
}
