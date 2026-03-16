package com.elanrif.springbootstarterkit.mapper;

import com.elanrif.springbootstarterkit.dto.user.UserCreateDto;
import com.elanrif.springbootstarterkit.dto.user.UserDto;
import com.elanrif.springbootstarterkit.dto.user.UserUpdateDto;
import com.elanrif.springbootstarterkit.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", expression = "java(dto.isActive() != null ? dto.isActive() : false)")
    User toEntity(UserCreateDto dto);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UserUpdateDto dto, @MappingTarget User entity);
}
