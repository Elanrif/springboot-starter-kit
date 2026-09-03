package com.elanrif.springbootstarterkit.mapper;

import com.elanrif.springbootstarterkit.dto.AuthDto;
import com.elanrif.springbootstarterkit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// ℹ️ MapStruct automatically maps properties when source and target property names match,
// including nested properties. ⛔ Non-matching properties are ignored.

@Mapper(componentModel = "spring")
public interface AuthMapper {

    // source (User) = the object MapStruct reads data from
    // target (AuthDto.Response) = the object MapStruct writes data to
    //
    // ⛔ Use @Mapping(target = "targetProperty", source = "sourceProperty")
    // when source and target property names differ.
    // Example: @Mapping(target = "author", source = "user")
    @Mapping(
            target = "numberOfAddresses",
            expression = "java(user.getAddresses() != null ? user.getAddresses().size() : 0)"
    )
    AuthDto.Response toDto(User user);
}
