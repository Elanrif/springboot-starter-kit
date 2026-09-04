package com.elanrif.springbootstarterkit.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public interface ProfileFields {
    @NotBlank @Size(max = 200) String firstName();
    @NotBlank @Size(max = 200) String lastName();
    @NotBlank @Email @Size(max = 255) String email();

    @NotBlank
    @Pattern(
            regexp = "^(?:(?:\\+212|00212)\\s?[5-7]\\d{2}\\s?\\d{3}\\s?" +
                    "\\d{3}|0[5-7]\\d{2}\\s?\\d{3}\\s?\\d{3})$",
            message = "must be a valid Moroccan phone number"
    )
    String phoneNumber();
}