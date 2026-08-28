package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.AddressDto;
import com.elanrif.springbootstarterkit.dto.CommonDto;
import com.elanrif.springbootstarterkit.entity.Address;
import com.elanrif.springbootstarterkit.entity.User;
import com.elanrif.springbootstarterkit.exception.BadRequestException;
import com.elanrif.springbootstarterkit.exception.ResourceNotFoundException;
import com.elanrif.springbootstarterkit.mapper.AddressMapper;
import com.elanrif.springbootstarterkit.repository.AddressRepository;
import com.elanrif.springbootstarterkit.repository.UserRepository;
import com.elanrif.springbootstarterkit.specification.AddressSpecification;
import com.elanrif.springbootstarterkit.util.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
    @Transactional
    public AddressDto.Response createUserAddress(
            Long userId,
            AddressDto.CreateRequest request
    ) {
        log.debug("Creating address for user {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", userId);
                    return new ResourceNotFoundException(
                            "User not found with id: " + userId
                    );
                });

        Address address = addressMapper.toEntity(request);
        address.setUser(user);

        boolean hasAddresses =
                addressRepository.existsByUserId(userId);

        if (!hasAddresses) {
            address.setDefaultAddress(true);
        } else if (Boolean.TRUE.equals(address.getDefaultAddress())) {
            resetDefaultAddresses(userId);
        }

        Address savedAddress =
                addressRepository.save(address);

        log.info(
                "Address created successfully with id: {} for user {}",
                savedAddress.getId(),
                userId
        );

        return addressMapper.toResponse(savedAddress);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AddressDto.Response> getAllAddresses(
            AddressDto.Filter filter,
            CommonDto.Pagination pagination
    ) {
        log.debug(
                "Fetching all addresses - page: {}, size: {}, country: {}, city: {}",
                pagination.page(),
                pagination.size(),
                filter != null ? filter.country() : null,
                filter != null ? filter.city() : null
        );

        Page<AddressDto.Response> addresses = addressRepository
                .findAll(
                        AddressSpecification.from(null, filter),
                        pagination.toPageable()
                )
                .map(addressMapper::toResponse);

        log.debug(
                "Found {} addresses (total: {})",
                addresses.getNumberOfElements(),
                addresses.getTotalElements()
        );

        return PageResponse.from(addresses);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AddressDto.Response> getAddressesByUserId(
            Long userId,
            AddressDto.Filter filter,
            CommonDto.Pagination pagination
    ) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
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
    public AddressDto.Response getAddressById(Long id) {
        log.debug("Fetching address with id: {}", id);

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Address not found with id: {}", id);
                    return new ResourceNotFoundException(
                            "Address not found with id: " + id
                    );
                });

        return addressMapper.toResponse(address);
    }

    @Override
    @Transactional
    public AddressDto.Response updateAddress(
            Long id,
            AddressDto.UpdateRequest request
    ) {
        log.debug("Updating address with id: {}", id);

        Address existingAddress = addressRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Address not found with id: {}", id);
                    return new ResourceNotFoundException(
                            "Address not found with id: " + id
                    );
                });

        addressMapper.updateFromRequest(
                request,
                existingAddress
        );

        if (Boolean.TRUE.equals(request.defaultAddress())
                && !Boolean.TRUE.equals(existingAddress.getDefaultAddress())) {

            Long userId = existingAddress.getUser().getId();
            resetDefaultAddresses(userId);
            existingAddress.setDefaultAddress(true);
        }

        Address updatedAddress =
                addressRepository.save(existingAddress);

        log.info(
                "Address updated successfully with id: {}",
                id
        );

        return addressMapper.toResponse(updatedAddress);
    }

    @Override
    @Transactional
    public AddressDto.Response setDefaultAddress(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> {
                    log.warn("Address not found with id: {}", addressId);
                    return new ResourceNotFoundException(
                            "Address not found with id: " + addressId
                    );
                });

        Long userId = address.getUser().getId();
        log.debug(
                "Setting address {} as default for user {}",
                addressId,
                userId
        );

        resetDefaultAddresses(userId);
        address.setDefaultAddress(true);
        Address updatedAddress =
                addressRepository.save(address);

        log.info(
                "Address {} set as default for user {}",
                addressId,
                userId
        );

        return addressMapper.toResponse(updatedAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(Long id) {
        log.debug("Deleting address with id: {}", id);

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Address not found with id: {}", id);
                    return new ResourceNotFoundException(
                            "Address not found with id: " + id
                    );
                });

        boolean isDefault =
                Boolean.TRUE.equals(address.getDefaultAddress());

        Long userId =
                address.getUser().getId();

        addressRepository.delete(address);

        if (isDefault) {
            addressRepository.findByUserId(userId)
                    .stream()
                    .findFirst()
                    .ifPresent(nextAddress -> {
                        nextAddress.setDefaultAddress(true);
                        addressRepository.save(nextAddress);
                    });
        }

        log.info("Address deleted successfully with id: {}", id);
    }

    private void resetDefaultAddresses(Long userId) {
        List<Address> addresses =
                addressRepository.findByUserId(userId);

        addresses.forEach(address -> {
            if (Boolean.TRUE.equals(address.getDefaultAddress())) {
                address.setDefaultAddress(false);
            }
        });

        addressRepository.saveAll(addresses);
    }
}