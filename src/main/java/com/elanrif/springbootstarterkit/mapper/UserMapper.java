package com.elanrif.springbootstarterkit.mapper;

import com.elanrif.springbootstarterkit.dto.UserDto;
import com.elanrif.springbootstarterkit.entity.User;
import org.mapstruct.*;

// ℹ️ MapStruct automatically maps properties when source and target property names match,
// including nested properties. ⛔ Non-matching properties are ignored.

@Mapper(componentModel = "spring")
public interface UserMapper {

    // source (User) = the object MapStruct reads data from
    // target (UserDto.Response) = the object MapStruct writes data to
    //
    // ⛔ Use @Mapping(target = "targetProperty", source = "sourceProperty")
    // when source and target property names differ.
    // Example: @Mapping(target = "author", source = "user")
    @Mapping(
            target = "numberOfAddresses",
            expression = "java(user.getAddresses() != null ? user.getAddresses().size() : 0)"
    )
    UserDto.Response toDto(User user);

    // source (UserDto.Request) = the object MapStruct reads data from
    // target (User) = the object MapStruct writes data to
    //
    // "ignore = true" means that this target property must not be mapped.
    @Mapping(target = "id", ignore = true)
    User toEntity(UserDto.Request request);

    // source (UserDto.Request) = the object MapStruct reads data from
    // target (User) = the existing object MapStruct writes data to
    //
    // Null values are ignored, so existing target values are kept
    // when the corresponding source property is null.
    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    void updateEntity(
            UserDto.Request request, // The parameter without @MappingTarget is the source.
            @MappingTarget User user // @MappingTarget identifies the target to be modified.
    );
}
