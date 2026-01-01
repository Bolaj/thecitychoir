package com.portfolio.thecitychoir.dto;

import java.time.LocalDateTime;

public record AttendanceListItemDto(
        String fullName,
        String registrationNumber,
        String part,
        LocalDateTime checkedAt,
        String deviceId
) {

}
