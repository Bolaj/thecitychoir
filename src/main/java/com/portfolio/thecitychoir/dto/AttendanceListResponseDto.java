package com.portfolio.thecitychoir.dto;

import java.time.LocalDate;
import java.util.List;

public record AttendanceListResponseDto(
        Long rehearsalId,
        LocalDate rehearsalDate,
        int totalPresent,
        List<AttendanceListItemDto> attendees
) {
}
