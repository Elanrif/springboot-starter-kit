package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.entity.Address;
import java.util.List;

public interface AddressService {

    List<Address> getAddressesByUserId(Long userId);

    Address getDefaultAddressByUserId(Long userId);

    Address getAddressById(Long id);

    Address createAddress(Long userId, Address address);

    Address updateAddress(Long id, Address addressDetails);

    Address setDefaultAddress(Long userId, Long addressId);

    void deleteUserAddress(Long userId, Long addressId);

    void deleteAddress(Long id);
}