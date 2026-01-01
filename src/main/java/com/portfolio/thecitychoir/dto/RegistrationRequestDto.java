package com.portfolio.thecitychoir.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RegistrationRequestDto (


        @NotBlank(message = "Full name is required")
        String fullName,

        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,

        @NotBlank(message = "Gender is required")
        String gender,
        @NotBlank(message = "Choir part is required")
        String part,

        @NotBlank(message = "Phone is required")
        @Size(min = 11, max = 13, message = "Phone number must be between 10 and 15 digits")
        String phone
) {}

