package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.entity.Address;
import com.elanrif.springbootstarterkit.entity.User;
import com.elanrif.springbootstarterkit.repository.AddressRepository;
import com.elanrif.springbootstarterkit.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Address> getAddressesByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }
        return addressRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Address getDefaultAddressByUserId(Long userId) {
        return addressRepository.findByUserId(userId).stream()
                .filter(Address::isDefault)
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
            address.setDefault(true);
        } else if (address.isDefault()) {
            // Si l'utilisateur demande à ce qu'elle soit par défaut, on désactive les anciennes
            resetDefaultAddresses(userId);
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
        if (addressDetails.isDefault() && !address.isDefault()) {
            resetDefaultAddresses(address.getUser().getId());
            address.setDefault(true);
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

        resetDefaultAddresses(userId);
        address.setDefault(true);

        return addressRepository.save(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Long id) {
        Address address = getAddressById(id);

        // Si on supprime l'adresse par défaut, on essaie de passer une autre adresse par défaut si elle existe
        if (address.isDefault()) {
            Long userId = address.getUser().getId();
            addressRepository.delete(address);

            addressRepository.findByUserId(userId).stream()
                    .findFirst()
                    .ifPresent(nextAddress -> {
                        nextAddress.setDefault(true);
                        addressRepository.save(nextAddress);
                    });
        } else {
            addressRepository.delete(address);
        }
    }

    // Méthode utilitaire interne pour passer toutes les adresses d'un utilisateur à false
    private void resetDefaultAddresses(Long userId) {
        List<Address> userAddresses = addressRepository.findByUserId(userId);
        for (Address addr : userAddresses) {
            if (addr.isDefault()) {
                addr.setDefault(false);
                addressRepository.save(addr);
            }
        }
    }
}