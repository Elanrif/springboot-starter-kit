package com.elanrif.springbootstarterkit.mapper;

import com.elanrif.springbootstarterkit.dto.AddressDto;
import com.elanrif.springbootstarterkit.dto.UserDto;
import com.elanrif.springbootstarterkit.entity.User;
import com.elanrif.springbootstarterkit.util.PageResponse;
import org.mapstruct.*;

/**
 * uses = {AddressMapper.class}
 *
 * Indique à MapStruct d'utiliser explicitement AddressMapper pour le mapping des objets Address.
 *
 * Sans cette configuration, MapStruct génère un mapping implicite pour Address
 * lors du mapping de User -> UserDto.Response, ce qui peut entraîner :
 * - des champs imbriqués mal résolus (ex: userId = null)
 * - une perte des règles de mapping définies dans AddressMapper
 *
 * Avec "uses", MapStruct délègue entièrement le mapping de Address à AddressMapper,
 * garantissant une cohérence et une réutilisation des règles de transformation.
 */
@Mapper(componentModel = "spring", uses = AddressMapper.class)
public interface UserMapper {

    UserDto.Summary toSummary(User user);

    @Mapping(target = "addrSize", expression = "java(user.getAddresses() != null ? user.getAddresses().size() : 0)")
    UserDto.Response toResponse(User user);

    @Mapping(target = "addresses", source = "addresses")
    UserDto.AddressesResponse toAddressesResponse(
            User user,
            PageResponse<AddressDto.Response> addresses
    );

    @Mapping(target = "id", ignore = true)
    User toEntity(UserDto.CreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromRequest(UserDto.UpdateRequest request, @MappingTarget User entity);
}
