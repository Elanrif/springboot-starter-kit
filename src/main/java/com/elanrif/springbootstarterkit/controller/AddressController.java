package com.elanrif.springbootstarterkit.controller;

import com.elanrif.springbootstarterkit.dto.AddressDto;
import com.elanrif.springbootstarterkit.mapper.AddressMapper; // 👈 Import du mapper
import com.elanrif.springbootstarterkit.services.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final AddressMapper addressMapper; // 👈 Injection du mapper

    @PostMapping("/user/{userId}")
    public ResponseEntity<AddressDto.Response> createAddress(
            @PathVariable Long userId,
            @Valid @RequestBody AddressDto.CreateRequest request) {
        log.info("POST /api/v1/addresses/user/{} - Creating address", userId);

        var addressEntity = addressMapper.toEntity(request); // 👈 Utilisation ici
        var savedAddress = addressService.createAddress(userId, addressEntity);

        return ResponseEntity.status(HttpStatus.CREATED).body(addressMapper.toResponse(savedAddress));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AddressDto.Response> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressDto.UpdateRequest request) {
        log.info("PUT /api/v1/addresses/{} - Updating address", id);

        // 1. On récupère d'abord l'entité existante depuis la base de données
        var existingAddress = addressService.getAddressById(id);

        // 2. On utilise MapStruct pour fusionner les modifications du 'request' dans l'entité existante
        addressMapper.updateFromRequest(request, existingAddress);

        // 3. On envoie l'entité fusionnée au service pour la sauvegarde
        var updatedAddress = addressService.updateAddress(id, existingAddress);

        return ResponseEntity.ok(addressMapper.toResponse(updatedAddress));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AddressDto.Response>> getAddressesByUserId(@PathVariable Long userId) {
        log.info("GET /api/v1/addresses/user/{} - Fetching all addresses", userId);

        List<AddressDto.Response> responses = addressService.getAddressesByUserId(userId).stream()
                .map(addressMapper::toResponse) // 👈 Utilisation ici
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}/default")
    public ResponseEntity<AddressDto.Response> getDefaultAddressByUserId(@PathVariable Long userId) {
        var defaultAddress = addressService.getDefaultAddressByUserId(userId);
        return ResponseEntity.ok(addressMapper.toResponse(defaultAddress));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressDto.Response> getById(@PathVariable Long id) {
        var address = addressService.getAddressById(id);
        return ResponseEntity.ok(addressMapper.toResponse(address));
    }

    @PatchMapping("/user/{userId}/default/{addressId}")
    public ResponseEntity<AddressDto.Response> setDefaultAddress(@PathVariable Long userId, @PathVariable Long addressId) {
        var updatedAddress = addressService.setDefaultAddress(userId, addressId);
        return ResponseEntity.ok(addressMapper.toResponse(updatedAddress));
    }

    @DeleteMapping("/user/{userId}/address/{addressId}")
    public ResponseEntity<Void> deleteUserAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId
    ) {
        addressService.deleteUserAddress(userId, addressId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}