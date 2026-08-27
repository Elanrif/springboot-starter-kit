package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.AddressDto;
import com.elanrif.springbootstarterkit.dto.CommonDto;
import com.elanrif.springbootstarterkit.dto.UserDto;
import com.elanrif.springbootstarterkit.entity.Address;
import com.elanrif.springbootstarterkit.entity.User;
import com.elanrif.springbootstarterkit.mapper.AddressMapper;
import com.elanrif.springbootstarterkit.repository.AddressRepository;
import com.elanrif.springbootstarterkit.repository.UserRepository;
import com.elanrif.springbootstarterkit.specification.AddressSpecification;
import com.elanrif.springbootstarterkit.specification.UserSpecification;
import com.elanrif.springbootstarterkit.util.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AddressDto.Response> getAddressesByUserId(
            Long userId,
            AddressDto.Filter filter,
            CommonDto.Pagination pagination
    ) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(
                    "User not found with id: " + userId
            );
        }

        log.debug(
                "Fetching addresses for user {} - page: {}, size: {}, country: {}, city: {}",
                userId,
                pagination.page(),
                pagination.size(),
                filter != null ? filter.country() : null,
                filter != null ? filter.city() : null
        );

        Page<AddressDto.Response> addresses = addressRepository
                .findAll(
                        AddressSpecification.from(userId, filter),
                        pagination.toPageable()
                )
                .map(addressMapper::toResponse);

        log.debug(
                "Found {} addresses for user {} (total: {})",
                addresses.getNumberOfElements(),
                userId,
                addresses.getTotalElements()
        );

        return PageResponse.from(addresses);
    }

    @Override
    @Transactional(readOnly = true)
    public Address getDefaultAddressByUserId(Long userId) {
        return addressRepository.findByUserId(userId).stream()
                .filter(Address::getDefaultAddress)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("No default address found for user: " + userId));
    }

    @Override
    @Transactional(readOnly = true)
    public Address getAddressById(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found with id: " + id));
    }

    @Override
    @Transactional
    public Address createAddress(Long userId, Address address) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        address.setUser(user);

        // Si c'est la première adresse de l'utilisateur, on la force par défaut
        boolean hasAddresses = addressRepository.existsByUserId(userId);
        if (!hasAddresses) {
            address.setDefaultAddress(true);
        } else if (address.getDefaultAddress()) {
            // Si l'utilisateur demande à ce qu'elle soit par défaut, on désactive les anciennes
            resetgetDefaultAddresses(userId);
        }

        return addressRepository.save(address);
    }

    @Override
    @Transactional
    public Address updateAddress(Long id, Address addressDetails) {
        Address address = getAddressById(id);

        address.setStreet(addressDetails.getStreet());
        address.setPostalCode(addressDetails.getPostalCode());
        address.setCity(addressDetails.getCity());
        address.setCountry(addressDetails.getCountry());

        // Gestion du changement d'adresse par défaut lors de l'update
        if (addressDetails.getDefaultAddress() && !address.getDefaultAddress()) {
            resetgetDefaultAddresses(address.getUser().getId());
            address.setDefaultAddress(true);
        }

        return addressRepository.save(address);
    }

    @Override
    @Transactional
    public Address setDefaultAddress(Long userId, Long addressId) {
        Address address = getAddressById(addressId);

        if (!address.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Address does not belong to this user");
        }

        resetgetDefaultAddresses(userId);
        address.setDefaultAddress(true);

        return addressRepository.save(address);
    }

    @Override
    public void deleteUserAddress(Long userId, Long addressId) {
        Address address = getAddressById(addressId);

        if (!address.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Address does not belong to this user");
        }

        addressRepository.delete(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Long id) {
        Address address = getAddressById(id);

        // Si on supprime l'adresse par défaut, on essaie de passer une autre adresse par défaut si elle existe
        if (address.getDefaultAddress()) {
            Long userId = address.getUser().getId();
            addressRepository.delete(address);

            addressRepository.findByUserId(userId).stream()
                    .findFirst()
                    .ifPresent(nextAddress -> {
                        nextAddress.setDefaultAddress(true);
                        addressRepository.save(nextAddress);
                    });
        } else {
            addressRepository.delete(address);
        }
    }

    // Méthode utilitaire interne pour passer toutes les adresses d'un utilisateur à false
    private void resetgetDefaultAddresses(Long userId) {
        List<Address> userAddresses = addressRepository.findByUserId(userId);
        for (Address addr : userAddresses) {
            if (addr.getDefaultAddress()) {
                addr.setDefaultAddress(false);
                addressRepository.save(addr);
            }
        }
    }
}