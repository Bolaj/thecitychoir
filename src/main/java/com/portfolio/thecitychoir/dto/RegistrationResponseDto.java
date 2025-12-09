package com.portfolio.thecitychoir.dto;

import lombok.Builder;

@Builder
public record RegistrationResponseDto(
        String fullName,
        String email,
        String part,
        String registrationNumber
) {}

