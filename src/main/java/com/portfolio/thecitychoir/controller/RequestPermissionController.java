package com.portfolio.thecitychoir.controller;

import com.portfolio.thecitychoir.dto.DecidePermissionRequestDto;
import com.portfolio.thecitychoir.dto.PermissionListItemDto;
import com.portfolio.thecitychoir.dto.PermissionResponseDto;
import com.portfolio.thecitychoir.dto.RequestPermissionRequestDto;
import com.portfolio.thecitychoir.service.RequestPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth/permission")
@RequiredArgsConstructor
@Tag(name = "Request Permissions", description = "Endpoints for requesting and managing permissions")
public class RequestPermissionController {

    private final RequestPermissionService permissionService;


    @GetMapping("/list")
    @Operation(
            summary = "List all permission requests",
            description = "Members see their own requests; Directors/Admins see all requests.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<List<PermissionListItemDto>> listPermissions(Authentication authentication) {
        String email = authentication.getName();
        List<PermissionListItemDto> permissions = permissionService.listPermissions(email);
        return ResponseEntity.ok(permissions);
    }

    @PostMapping("/request")
    @Operation(
            summary = "Submit a permission request",
            description = "Allows a logged-in member to request permission for a specific absence date.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<PermissionResponseDto> submitPermissionRequest(
            @RequestBody @Valid RequestPermissionRequestDto requestDto,
            Authentication authentication
    ) {
        String memberEmail = authentication.getName();

        var permission = permissionService.requestPermission(
                memberEmail,
                requestDto.getReason(),
                requestDto.getAbsenceDate()
        );
        PermissionResponseDto response = new PermissionResponseDto(
                permission.getId(),
                permission.getStatus(),
                "Permission request submitted successfully"
        );

        return ResponseEntity.ok(response);
    }


    @PostMapping("/decide/{permissionId}")
    @Operation(
            summary = "Approve or reject a permission",
            description = "Allows a DIRECTOR or ADMIN to approve or reject a permission request.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<PermissionResponseDto> decidePermission(
            @PathVariable Long permissionId,
            @RequestBody @Valid DecidePermissionRequestDto requestDto,
            Authentication authentication
    ) {
        String approverEmail = authentication.getName();

        String message = permissionService.decidePermission(
                permissionId,
                requestDto.getDecision(),
                approverEmail
        );

        PermissionResponseDto response = new PermissionResponseDto(
                permissionId,
                requestDto.getDecision(),
                message
        );

        return ResponseEntity.ok(response);
    }


}
