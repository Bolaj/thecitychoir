package com.portfolio.thecitychoir.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CreateRehearsalDto(
        @NotBlank String name,
        @NotNull Double lat,
        @NotNull Double lng,
        @NotNull Integer radiusMeters,
        @NotNull LocalDateTime startTime,
        @NotNull LocalDateTime endTime,
        @NotNull LocalDate rehearsalDate
) {}
