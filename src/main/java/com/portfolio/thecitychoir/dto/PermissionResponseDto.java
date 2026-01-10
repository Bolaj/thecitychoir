package com.portfolio.thecitychoir.dto;

import com.portfolio.thecitychoir.permission.PermissionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PermissionResponseDto {
    private Long permissionId;
    private PermissionStatus status;
    private String message;
}
