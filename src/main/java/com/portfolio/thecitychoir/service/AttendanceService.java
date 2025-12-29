package com.portfolio.thecitychoir.service;

import com.portfolio.thecitychoir.dto.AttendanceRequestDto;
import com.portfolio.thecitychoir.dto.AttendanceResponseDto;
import com.portfolio.thecitychoir.dto.CreateRehearsalDto;
import com.portfolio.thecitychoir.entity.AttendanceEntity;
import com.portfolio.thecitychoir.entity.DeviceBindingEntity;
import com.portfolio.thecitychoir.entity.ProfileEntity;
import com.portfolio.thecitychoir.entity.RehearsalEntity;
import com.portfolio.thecitychoir.exceptions.InvalidRehearsalTimeException;
import com.portfolio.thecitychoir.exceptions.NoActiveRehearsalException;
import com.portfolio.thecitychoir.exceptions.RehearsalAlreadyExistsException;
import com.portfolio.thecitychoir.repository.AttendanceRepository;
import com.portfolio.thecitychoir.repository.DeviceBindRepository;
import com.portfolio.thecitychoir.repository.ProfileRepository;
import com.portfolio.thecitychoir.repository.RehearsalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final RehearsalRepository rehearsalRepository;
    private final DeviceBindRepository deviceBindingRepository;
    private final ProfileRepository profileRepository;

    private static final double EARTH_RADIUS_METERS = 6_371_000d;

    @Transactional
    public AttendanceResponseDto markAttendance(String authEmail, AttendanceRequestDto req) {
        ProfileEntity user = profileRepository.findByEmail(authEmail)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        if (!user.getRegistrationNumber().equals(req.registrationNumber())) {
            return new AttendanceResponseDto(false, "You can only mark attendance for your account", null, null);
        }

        LocalDateTime now = LocalDateTime.now();
        RehearsalEntity rehearsal = rehearsalRepository.findAll().stream()
                .filter(r -> isWithinWindow(r.getStartTime(), r.getEndTime(), now))
                .findFirst()
                .orElseThrow(() -> new NoActiveRehearsalException("No active rehearsal right now"));

        if (attendanceRepository.existsByProfileAndRehearsal(user, rehearsal)) {
            return new AttendanceResponseDto(false, "Attendance already recorded for this rehearsal", user.getRegistrationNumber(), null);
        }

        DeviceBindingEntity binding = deviceBindingRepository.findByProfileAndDeviceId(user, req.deviceId())
                .orElseGet(() -> {
                    DeviceBindingEntity b = new DeviceBindingEntity();
                    b.setProfile(user);
                    b.setDeviceId(req.deviceId());
                    b.setDeviceInfo("auto-bound");
                    b.setApproved(true); // change to false if admin approval desired
                    b.setBoundAt(LocalDateTime.now());
                    return deviceBindingRepository.save(b);
                });

        if (!binding.isApproved()) {
            return new AttendanceResponseDto(false, "Device not approved", user.getRegistrationNumber(), null);
        }

        if (Boolean.TRUE.equals(req.isMock())) {
            return new AttendanceResponseDto(false, "Mock location detected; attendance denied", user.getRegistrationNumber(), null);
        }

        double distance = distanceInMeters(rehearsal.getLat(), rehearsal.getLng(), req.lat(), req.lng());
        if (distance > rehearsal.getRadiusMeters()) {
            return new AttendanceResponseDto(false, "You are outside allowed radius (" + Math.round(distance) + "m)", user.getRegistrationNumber(), null);
        }

        if (rehearsal.getCurrentQrToken() != null) {
            if (req.qrToken() == null || !req.qrToken().equals(rehearsal.getCurrentQrToken()) || rehearsal.getCurrentQrExpiresAt().isBefore(now)) {
                return new AttendanceResponseDto(false, "Invalid or expired QR token", user.getRegistrationNumber(), null);
            }
        }

        AttendanceEntity attendance = new AttendanceEntity();
        attendance.setProfile(user);
        attendance.setRehearsal(rehearsal);
        attendance.setCheckedAt(now);
        attendance.setLat(req.lat());
        attendance.setLng(req.lng());
        attendance.setDeviceId(req.deviceId());
        attendance.setQrToken(req.qrToken());
        attendanceRepository.save(attendance);

        return new AttendanceResponseDto(true, "Attendance Marked Successfully", user.getRegistrationNumber(), attendance.getCheckedAt());
    }

    @Transactional
    public RehearsalEntity createRehearsal(CreateRehearsalDto dto) {

        LocalDate date = dto.startTime().toLocalDate();
        if (dto.startTime().isBefore(LocalDateTime.now()) || dto.endTime().isBefore(LocalDateTime.now())) {
            throw new InvalidRehearsalTimeException("Start and end time cannot be in the past");
        }

        if (rehearsalRepository.existsByRehearsalDate(date)) {
            throw new RehearsalAlreadyExistsException(
                    "A rehearsal already exists for " + date
            );
        }

        return rehearsalRepository.save(
                RehearsalEntity.builder()
                        .name(dto.name())
                        .lat(dto.lat())
                        .lng(dto.lng())
                        .radiusMeters(dto.radiusMeters())
                        .startTime(dto.startTime())
                        .endTime(dto.endTime())
                        .rehearsalDate(date)
                        .build()
        );
    }


    @Transactional
    public String rotateQrToken(Long rehearsalId, int validMinutes) {
        RehearsalEntity r = rehearsalRepository.findById(rehearsalId)
                .orElseThrow(() -> new RuntimeException("Rehearsal not found"));
        String token = UUID.randomUUID().toString().replace("-", "");
        r.setCurrentQrToken(token);
        r.setCurrentQrExpiresAt(LocalDateTime.now().plusMinutes(validMinutes));
        rehearsalRepository.save(r);
        return token;
    }

    private boolean isWithinWindow(LocalDateTime start, LocalDateTime end, LocalDateTime now){
        return (now.isEqual(start) || now.isAfter(start)) && (now.isBefore(end) || now.isEqual(end));
    }

    private double distanceInMeters(double lat1, double lon1, double lat2, double lon2){
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return EARTH_RADIUS_METERS * c;
    }
}


