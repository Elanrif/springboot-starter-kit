package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.AddressDto;
import com.elanrif.springbootstarterkit.dto.CommonDto;
import com.elanrif.springbootstarterkit.entity.Address;
import com.elanrif.springbootstarterkit.util.PageResponse;

public interface AddressService {
    // =========================================================
    // CRUD operations for Address entity
    // =========================================================

    AddressDto.Response createUserAddress(
            Long userId,
            AddressDto.CreateRequest request
    );

    PageResponse<AddressDto.Response> getAllAddresses(
            AddressDto.Filter filter,
            CommonDto.Pagination pagination
    );

    PageResponse<AddressDto.Response> getAddressesByUserId(
            Long userId,
            AddressDto.Filter filter,
            CommonDto.Pagination pagination
    );

    AddressDto.Response getAddressById(Long id);

    AddressDto.Response updateAddress(
            Long id,
            AddressDto.UpdateRequest request
    );

    AddressDto.Response setDefaultAddress(Long addressId);

    void deleteAddress(Long id);
}