package com.portfolio.thecitychoir.controller;

import com.portfolio.thecitychoir.dto.*;
import com.portfolio.thecitychoir.entity.RehearsalEntity;
import com.portfolio.thecitychoir.service.AttendanceService;
import com.portfolio.thecitychoir.service.ProfileService;
import com.portfolio.thecitychoir.util.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance", description = "Endpoints for marking attendance, rehearsals, summaries and QR tokens")
@SecurityRequirement(name = "bearerAuth")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final ProfileService profileService;
    private final JWTUtil jwtUtil;

    @Operation(
            summary = "Mark attendance",
            description = "Marks attendance for the logged-in user using a valid JWT token"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Attendance marked successfully",
                    content = @Content(schema = @Schema(implementation = AttendanceResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "User not allowed to mark attendance", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized / invalid token", content = @Content)
    })
    @PostMapping("/mark")
    public ResponseEntity<?> markAttendance(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody AttendanceRequestDto req) {

        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractUsername(token);

        AttendanceResponseDto res = attendanceService.markAttendance(email, req);

        return res.success()
                ? ResponseEntity.ok(res)
                : ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
    }

    @Operation(
            summary = "Create a rehearsal",
            description = "Creates a new rehearsal session (ADMIN only)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rehearsal created successfully",
                    content = @Content(schema = @Schema(implementation = RehearsalEntity.class))),
            @ApiResponse(responseCode = "403", description = "Access denied (not ADMIN)", content = @Content)
    })
    @PostMapping("/rehearsal")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RehearsalEntity> createRehearsal(
            @Valid @RequestBody CreateRehearsalDto dto
    ) {
        return ResponseEntity.ok(attendanceService.createRehearsal(dto));
    }

    @Operation(
            summary = "Get attendance list for a rehearsal",
            description = "Returns all attendance records for a specific rehearsal (ADMIN only)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Attendance list returned",
                    content = @Content(schema = @Schema(implementation = AttendanceListResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Rehearsal not found", content = @Content)
    })
    @GetMapping("/rehearsals/{rehearsalId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AttendanceListResponseDto> getAttendance(
            @PathVariable Long rehearsalId
    ) {
        return ResponseEntity.ok(attendanceService.getAttendanceForRehearsal(rehearsalId));
    }

    @Operation(
            summary = "Get attendance summary",
            description = "Returns summary stats (present/absent/late) for a rehearsal (ADMIN only)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Attendance summary returned",
                    content = @Content(schema = @Schema(implementation = AttendanceSummaryResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Rehearsal not found", content = @Content)
    })
    @GetMapping("/rehearsals/{rehearsalId}/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AttendanceSummaryResponseDto> getSummary(
            @PathVariable Long rehearsalId
    ) {
        AttendanceSummaryResponseDto summary = attendanceService.getAttendanceSummary(rehearsalId);
        return ResponseEntity.ok(summary);
    }

    @Operation(
            summary = "Rotate QR token",
            description = "Generates a new QR token for a rehearsal with an expiry time (ADMIN only)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "QR token rotated successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Rehearsal not found", content = @Content)
    })
    @PostMapping("/rehearsal/{id}/rotate-qr")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rotateQr(
            @PathVariable Long id,
            @RequestParam(defaultValue = "5") int minutes
    ) {
        String token = attendanceService.rotateQrToken(id, minutes);
        return ResponseEntity.ok(Map.of(
                "qrToken", token,
                "expiresInMinutes", minutes
        ));
    }
}
