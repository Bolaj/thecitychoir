package com.portfolio.thecitychoir.dto;

import lombok.Builder;

@Builder
public record RegistrationRequestDto (
        String fullName,
        String email,
        String password,
        String gender,
        String part,
        String phone
) {}
