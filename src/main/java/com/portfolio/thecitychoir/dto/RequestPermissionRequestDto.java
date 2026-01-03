package com.portfolio.thecitychoir.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfolio.thecitychoir.permission.PermissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestPermissionRequestDto {
    private String reason;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate absenceDate;
}
