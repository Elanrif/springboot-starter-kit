package com.elanrif.springbootstarterkit.mapper;

import com.elanrif.springbootstarterkit.dto.AuthDto;
import com.elanrif.springbootstarterkit.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    AuthDto.Response toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toEntity(AuthDto.RegisterRequest request);
}
