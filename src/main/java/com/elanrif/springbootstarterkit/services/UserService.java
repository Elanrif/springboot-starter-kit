package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.PaginationDto;
import com.elanrif.springbootstarterkit.dto.UserDto;
import com.elanrif.springbootstarterkit.dto.shared.PageResponse;

public interface UserService {
    PageResponse<UserDto.Response> getUsers(
            UserDto.Filter filter,
            PaginationDto.Pagination pagination
    );
    UserDto.Response getById(Long id);

    UserDto.Response createUser(UserDto.Request request);

    UserDto.Response updateUser(
            Long id,
            UserDto.Request request
    );
    void deleteUser(Long id);

    void purgeUser(Long id);
}
