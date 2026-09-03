package com.elanrif.springbootstarterkit.mapper;

import com.elanrif.springbootstarterkit.dto.AddressDto;
import com.elanrif.springbootstarterkit.entity.Address;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

// ℹ️ MapStruct automatically maps properties when source and target property names match,
// including nested properties. ⛔ Non-matching properties are ignored.

@Mapper(componentModel = "spring")
public interface AddressMapper {

    // source (Address) = the object MapStruct reads data from
    // target (AddressDto.Response) = the object MapStruct writes data to
    //
    // ⛔ Use @Mapping(target = "targetProperty", source = "sourceProperty")
    // when source and target property names differ.
    // Example: @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userId", source = "user.id")
    AddressDto.Response toDto(Address address);


    // source (AddressDto.CreateRequest) = the object MapStruct reads data from
    // target (Address) = the object MapStruct writes data to
    //
    // "ignore = true" means that this target property must not be mapped.
    @Mapping(target = "id", ignore = true)
    Address toEntity(AddressDto.CreateRequest request);

    // source (AddressDto.UpdateRequest) = the object MapStruct reads data from
    // target (Address) = the existing object MapStruct writes data to
    //
    // "ignore = true" means that this target property must not be mapped.
    // Null values are ignored, so existing target values are kept
    // when the corresponding source property is null.
    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    void updateEntity(
            AddressDto.UpdateRequest request, // The parameter without @MappingTarget is the source.
            @MappingTarget Address address // @MappingTarget identifies the target to be modified.
    );
}