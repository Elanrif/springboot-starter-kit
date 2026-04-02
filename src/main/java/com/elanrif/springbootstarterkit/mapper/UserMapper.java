package com.elanrif.springbootstarterkit.mapper;

import com.elanrif.springbootstarterkit.dto.UserDto;
import com.elanrif.springbootstarterkit.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto.Summary toSummary(User user);

    UserDto.Response toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", expression = "java(request.isActive() != null ? request.isActive() : false)")
    User toEntity(UserDto.CreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromRequest(UserDto.UpdateRequest request, @MappingTarget User entity);
}
