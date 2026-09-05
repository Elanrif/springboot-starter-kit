package com.elanrif.springbootstarterkit.controller.auth;

import com.elanrif.springbootstarterkit.dto.auth.CurrentUserDto;
import com.elanrif.springbootstarterkit.dto.UserDto;
import com.elanrif.springbootstarterkit.services.auth.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
@Tag(name = "Account", description = "Management of the currently authenticated user's account")
public class CurrentUserController {

    private final CurrentUserService currentUserService;

    @PatchMapping
    @Operation(
            summary = "Update my profile",
            description = "Updates the profile fields of the currently authenticated user."
    )
    public ResponseEntity<UserDto.Response> updateMyProfile(
            @Valid @RequestBody CurrentUserDto.UpdateProfileRequest request
    ) {
        UserDto.Response response = currentUserService.updateMyProfile(request);
        log.info("PATCH /api/v1/account - Profile updated for user id: {}", response.id());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    @Operation(
            summary = "Change my password",
            description = "Changes the password of the currently authenticated user. Requires the current password."
    )
    public ResponseEntity<Void> changeMyPassword(
            @Valid @RequestBody CurrentUserDto.ChangePasswordRequest request
    ) {
        currentUserService.changeMyPassword(request);
        log.info("POST /api/v1/account/change-password - Password changed");
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @Operation(
            summary = "Delete my account",
            description = "Deletes the account of the currently authenticated user. Requires a confirmation message."
    )
    public ResponseEntity<Void> deleteMyAccount(
            @Valid @RequestBody CurrentUserDto.DeleteAccountRequest request
    ) {
        currentUserService.deleteMyAccount(request);
        log.info("DELETE /api/v1/account - Account deleted");
        return ResponseEntity.noContent().build();
    }
}