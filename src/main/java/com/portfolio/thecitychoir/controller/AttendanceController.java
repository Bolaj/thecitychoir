package com.portfolio.thecitychoir.controller;

import com.portfolio.thecitychoir.dto.AttendanceRequestDto;
import com.portfolio.thecitychoir.dto.AttendanceResponseDto;
import com.portfolio.thecitychoir.dto.CreateRehearsalDto;
import com.portfolio.thecitychoir.entity.RehearsalEntity;
import com.portfolio.thecitychoir.service.AttendanceService;
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
    private final JWTUtil jwtUtil; // used to extract username/email from token

    @PostMapping("/mark")
    public ResponseEntity<?> markAttendance(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody AttendanceRequestDto req) {

        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractUsername(token);

        AttendanceResponseDto res = attendanceService.markAttendance(email, req);

        return res.success() ? ResponseEntity.ok(res) : ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
    }

    // Admin: create rehearsal
    @PostMapping("/rehearsal")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RehearsalEntity> createRehearsal(@Valid @RequestBody CreateRehearsalDto dto) {
        RehearsalEntity r = new RehearsalEntity();
        r.setName(dto.name());
        r.setLat(dto.lat());
        r.setLng(dto.lng());
        r.setRadiusMeters(dto.radiusMeters());
        r.setStartTime(dto.startTime());
        r.setEndTime(dto.endTime());
        return ResponseEntity.ok(attendanceService.createRehearsal(r));
    }

    // Admin: rotate QR (returns token; frontend can render as QR image)
    @PostMapping("/rehearsal/{id}/rotate-qr")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rotateQr(@PathVariable Long id, @RequestParam(defaultValue = "5") int minutes) {
        String token = attendanceService.rotateQrToken(id, minutes);
        return ResponseEntity.ok(Map.of("qrToken", token, "expiresInMinutes", minutes));
    }
}
