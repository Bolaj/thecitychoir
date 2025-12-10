package com.portfolio.thecitychoir.dto;

import java.time.LocalDateTime;

public record AttendanceResponseDto(
        boolean success,
        String message,
        String registrationNumber,
        LocalDateTime checkedAt
) {}