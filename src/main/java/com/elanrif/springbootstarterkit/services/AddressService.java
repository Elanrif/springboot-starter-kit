package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.AddressDto;
import com.elanrif.springbootstarterkit.dto.PaginationDto;
import com.elanrif.springbootstarterkit.dto.shared.PageResponse;

public interface AddressService {

    PageResponse<AddressDto.Response> getAddresses(
            AddressDto.Filter filter,
            PaginationDto.Pagination pagination
    );

    AddressDto.Response getAddressById(Long id);

    AddressDto.Response createAddress(
            AddressDto.CreateRequest request
    );

    AddressDto.Response updateAddress(
            Long id,
            AddressDto.UpdateRequest request
    );

    void deleteAddress(Long id);
}