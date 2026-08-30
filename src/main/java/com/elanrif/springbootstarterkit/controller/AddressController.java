package com.elanrif.springbootstarterkit.controller;

import com.elanrif.springbootstarterkit.dto.AddressDto;
import com.elanrif.springbootstarterkit.dto.CommonDto;
import com.elanrif.springbootstarterkit.services.AddressService;
import com.elanrif.springbootstarterkit.util.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    // =========================================================
    // RCUD (Read,Create,Update,Delete) operations for User entity
    // =========================================================

    @GetMapping
    public ResponseEntity<PageResponse<AddressDto.Response>> getAddresses(
            @ModelAttribute AddressDto.Filter filter,
            @ModelAttribute CommonDto.Pagination pagination
    ) {
        log.info(
                "GET /api/v1/addresses - page: {}, size: {}",
                pagination.page(),
                pagination.size()
        );

        PageResponse<AddressDto.Response> response =
                addressService.getAddresses(filter, pagination);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressDto.Response> getAddress(
            @PathVariable Long id
    ) {
        log.info("GET /api/v1/addresses/{} - Fetching address", id);
        AddressDto.Response response = addressService.getAddressById(id);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<AddressDto.Response> createAddress(
            @Valid @RequestBody AddressDto.CreateRequest request
    ) {
        log.info(
                "POST /api/v1/addresses - Creating address"
        );
        AddressDto.Response response =
                addressService.createAddress(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AddressDto.Response> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressDto.UpdateRequest request
    ) {
        log.info(
                "PATCH /api/v1/addresses/{} - Updating address",
                id
        );

        AddressDto.Response response =
                addressService.updateAddress(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long id
    ) {
        log.info("DELETE /api/v1/addresses/{} - Deleting address", id);

        addressService.deleteAddress(id);

        return ResponseEntity.noContent().build();
    }
}