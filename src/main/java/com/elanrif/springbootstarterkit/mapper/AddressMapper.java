package com.elanrif.springbootstarterkit.mapper;

import com.elanrif.springbootstarterkit.dto.AddressDto;
import com.elanrif.springbootstarterkit.entity.Address;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "userId", source = "user.id")
    AddressDto.Response toResponse(Address address);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    Address toEntity(AddressDto.CreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateFromRequest(AddressDto.UpdateRequest request, @MappingTarget Address entity);
}