package com.portfolio.thecitychoir.dto;

import com.portfolio.thecitychoir.permission.PermissionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DecidePermissionRequestDto {
    @NotNull(message = "Decision is required")
    private PermissionStatus decision;
}
