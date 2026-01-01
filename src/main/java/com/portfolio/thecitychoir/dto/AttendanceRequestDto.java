package com.portfolio.thecitychoir.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AttendanceRequestDto(
        @NotBlank String registrationNumber,
        @NotNull Double lat,
        @NotNull Double lng,
        @NotBlank String deviceId,
        @NotNull Boolean isMock,
        String qrToken
) {}
