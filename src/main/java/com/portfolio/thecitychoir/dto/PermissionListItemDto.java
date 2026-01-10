package com.portfolio.thecitychoir.dto;

import com.portfolio.thecitychoir.permission.PermissionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PermissionListItemDto {
    private Long permissionId;
    private String memberEmail;
    private LocalDate absenceDate;
    private PermissionStatus status;
    private String reason;
}
