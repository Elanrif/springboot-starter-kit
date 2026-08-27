package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.AddressDto;
import com.elanrif.springbootstarterkit.dto.CommonDto;
import com.elanrif.springbootstarterkit.entity.Address;
import com.elanrif.springbootstarterkit.util.PageResponse;

import java.util.List;

public interface AddressService {

    PageResponse<AddressDto.Response> getAddressesByUserId(Long userId,
                                                           AddressDto.Filter filter,
                                                           CommonDto.Pagination pagination);

    Address getDefaultAddressByUserId(Long userId);

    Address getAddressById(Long id);

    Address createAddress(Long userId, Address address);

    Address updateAddress(Long id, Address addressDetails);

    Address setDefaultAddress(Long userId, Long addressId);

    void deleteUserAddress(Long userId, Long addressId);

    void deleteAddress(Long id);
}