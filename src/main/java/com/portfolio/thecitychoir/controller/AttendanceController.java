package com.portfolio.thecitychoir.controller;

import com.portfolio.thecitychoir.dto.AttendanceRequestDto;
import com.portfolio.thecitychoir.dto.AttendanceResponseDto;
import com.portfolio.thecitychoir.dto.CreateRehearsalDto;
import com.portfolio.thecitychoir.entity.ProfileEntity;
import com.portfolio.thecitychoir.entity.RehearsalEntity;
import com.portfolio.thecitychoir.role.Role;
import com.portfolio.thecitychoir.service.AttendanceService;
import com.portfolio.thecitychoir.service.ProfileService;
import com.portfolio.thecitychoir.util.JWTUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/auth/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final ProfileService profileService;
    private final JWTUtil jwtUtil;


    @PostMapping("/mark")
    public ResponseEntity<?> markAttendance(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody AttendanceRequestDto req) {

        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractUsername(token);

        AttendanceResponseDto res = attendanceService.markAttendance(email, req);

        return res.success() ? ResponseEntity.ok(res) : ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
    }

    @PutMapping("/role/{email}")
    @PreAuthorize("hasAuthority('Super Admin') or hasAuthority('Admin')")
    public ResponseEntity<ProfileEntity> updateProfileRole(
            @PathVariable String email,
            @RequestParam Role newRole  // make sure import is correct
    ) {
        ProfileEntity updatedProfile = profileService.updateRole(email, String.valueOf(newRole));
        return ResponseEntity.ok(updatedProfile);
    }

    @PostMapping("/rehearsal")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RehearsalEntity> createRehearsal(
            @Valid @RequestBody CreateRehearsalDto dto
    ) {
        return ResponseEntity.ok(attendanceService.createRehearsal(dto));
    }

    // Admin: rotate QR (returns token; frontend can render as QR image)
    @PostMapping("/rehearsal/{id}/rotate-qr")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rotateQr(@PathVariable Long id, @RequestParam(defaultValue = "5") int minutes) {
        String token = attendanceService.rotateQrToken(id, minutes);
        return ResponseEntity.ok(Map.of("qrToken", token, "expiresInMinutes", minutes));
    }


}
