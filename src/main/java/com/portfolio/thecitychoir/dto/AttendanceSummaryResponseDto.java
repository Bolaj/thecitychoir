package com.portfolio.thecitychoir.dto;

import java.time.LocalDate;
import java.util.List;

public record AttendanceSummaryResponseDto(
        Long rehearsalId,
        LocalDate rehearsalDate,
        Long totalPresent,
        List<AttendancePartCountDto> breakdown
) {
}
