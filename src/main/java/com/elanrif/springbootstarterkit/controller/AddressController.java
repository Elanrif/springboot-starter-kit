package com.elanrif.springbootstarterkit.controller;

import com.elanrif.springbootstarterkit.dto.AddressDto;
import com.elanrif.springbootstarterkit.dto.CommonDto;
import com.elanrif.springbootstarterkit.entity.Address;
import com.elanrif.springbootstarterkit.mapper.AddressMapper;
import com.elanrif.springbootstarterkit.services.AddressService;
import com.elanrif.springbootstarterkit.util.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final AddressMapper addressMapper;

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

        log.info(
                "Returned {} addresses (total: {})",
                response.data().size(),
                response.meta().total()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressDto.Response> getAddress(
            @PathVariable Long id
    ) {
        log.info("GET /api/v1/addresses/{} - Fetching address", id);

        AddressDto.Response response =
                addressMapper.toResponse(
                        addressService.getAddressById(id)
                );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AddressDto.Response> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressDto.UpdateRequest request
    ) {
        log.info("PATCH /api/v1/addresses/{} - Updating address", id);

        var address = addressService.getAddressById(id);

        // TODO: est il vraiment utilise cette ligne de code.
        addressMapper.updateFromRequest(request, address);

        var updatedAddress =
                addressService.updateAddress(id, address);

        return ResponseEntity.ok(
                addressMapper.toResponse(updatedAddress)
        );
    }

    @PatchMapping("/{id}/default")
    public ResponseEntity<AddressDto.Response> setDefaultAddress(
            @PathVariable Long id
    ) {
        log.info(
                "PATCH /api/v1/addresses/{}/default - Setting address as default",
                id
        );
        Address address = addressService.setDefaultAddress(id);

        return ResponseEntity.ok(
                addressMapper.toResponse(address)
        );
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