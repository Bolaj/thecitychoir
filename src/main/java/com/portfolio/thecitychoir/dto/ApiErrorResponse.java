package com.portfolio.thecitychoir.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ApiErrorResponse(
        int status,
        String error,
        String message,
        String path,
        LocalDateTime timestamp
) {}
