package com.elanrif.springbootstarterkit.controller;

import com.elanrif.springbootstarterkit.dto.AddressDto;
import com.elanrif.springbootstarterkit.dto.PaginationDto;
import com.elanrif.springbootstarterkit.services.AddressService;
import com.elanrif.springbootstarterkit.dto.shared.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Addresses", description = "Manage the authenticated user's shipping/billing addresses")
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    @Operation(summary = "List addresses", description = "Returns a paginated list of addresses, optionally filtered.")
    public ResponseEntity<PageResponse<AddressDto.Response>> getAddresses(
            @ModelAttribute AddressDto.Filter filter,
            @ModelAttribute PaginationDto.Pagination pagination
    ) {
        log.debug("GET /api/v1/addresses - page: {}, size: {}", pagination.page(), pagination.size());
        PageResponse<AddressDto.Response> response = addressService.getAddresses(filter, pagination);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an address by id", description = "Returns a single address. Returns 404 if it does not exist.")
    public ResponseEntity<AddressDto.Response> getAddress(@PathVariable Long id) {
        log.debug("GET /api/v1/addresses/{}", id);
        AddressDto.Response response = addressService.getAddressById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create an address", description = "Creates a new address record.")
    public ResponseEntity<AddressDto.Response> createAddress(
            @Valid @RequestBody AddressDto.CreateRequest request
    ) {
        AddressDto.Response response = addressService.createAddress(request);
        log.info("POST /api/v1/addresses - Address created with id: {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update an address", description = "Partially updates an existing address.")
    public ResponseEntity<AddressDto.Response> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressDto.UpdateRequest request
    ) {
        AddressDto.Response response = addressService.updateAddress(id, request);
        log.info("PATCH /api/v1/addresses/{} - Address updated", id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an address", description = "Deletes an address by id.")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        log.info("DELETE /api/v1/addresses/{} - Address deleted", id);
        return ResponseEntity.noContent().build();
    }
}
