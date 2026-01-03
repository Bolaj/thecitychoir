package com.portfolio.thecitychoir.service;

import com.portfolio.thecitychoir.dto.PermissionListItemDto;
import com.portfolio.thecitychoir.entity.ProfileEntity;
import com.portfolio.thecitychoir.entity.RequestPermissionEntity;
import com.portfolio.thecitychoir.permission.PermissionStatus;
import com.portfolio.thecitychoir.repository.RequestPermissionRepository;
import com.portfolio.thecitychoir.role.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestPermissionService {

    private final RequestPermissionRepository permissionRepository;
    private final ProfileService profileService;

    @Transactional
    public RequestPermissionEntity requestPermission(
            String loggedInEmail,
            String reason,
            java.time.LocalDate absenceDate
    ) {

        ProfileEntity member = profileService.getProfileByEmail(loggedInEmail);

        RequestPermissionEntity permission =
                RequestPermissionEntity.builder()
                        .profile(member)
                        .registrationNumber(member.getRegistrationNumber())
                        .reason(reason)
                        .absenceDate(absenceDate)
                        .status(PermissionStatus.PENDING)
                        .createdAt(LocalDateTime.now())
                        .build();

        permissionRepository.save(permission);

        log.info("Permission request submitted by {} for {}",
                member.getEmail(), absenceDate);

        return permission;
    }

    @Transactional
    public String decidePermission(
            Long permissionId,
            PermissionStatus decision,
            String approverEmail
    ) {

        ProfileEntity approver =
                profileService.getProfileByEmail(approverEmail);

        if (!approver.hasAnyRole(Role.DIRECTOR, Role.ADMIN)) {
            log.warn("Unauthorized permission decision attempt by {}", approverEmail);
            throw new AccessDeniedException("You are not allowed to decide permissions");
        }

        RequestPermissionEntity permission =
                permissionRepository.findById(permissionId)
                        .orElseThrow(() ->
                                new IllegalArgumentException("Permission not found"));

        if (permission.getStatus() == PermissionStatus.APPROVED) {
            return "Permission already approved";
        }

        if (permission.getStatus() == PermissionStatus.REJECTED) {
            return "Sorry, your permission was declined";
        }

        permission.setStatus(decision);
        permission.setDecidedBy(approver);
        permission.setDecidedAt(LocalDateTime.now());

        permissionRepository.save(permission);

        log.info("Permission {} by {} for member {}",
                decision,
                approver.getEmail(),
                permission.getProfile().getEmail()
        );

        return decision == PermissionStatus.APPROVED
                ? "Permission approved"
                : "Sorry, your permission was declined";
    }
    @Transactional(readOnly = true)
    public List<PermissionListItemDto> listPermissions(String requesterEmail) {
        ProfileEntity requester = profileService.getProfileByEmail(requesterEmail);

        boolean isDirectorOrAdmin = requester.hasAnyRole(Role.DIRECTOR, Role.ADMIN);

        List<RequestPermissionEntity> permissions;

        if (isDirectorOrAdmin) {
            permissions = permissionRepository.findAll();
        } else {
            permissions = permissionRepository.findByProfile(requester);
        }

        return permissions.stream()
                .map(p -> new PermissionListItemDto(
                        p.getId(),
                        p.getProfile().getEmail(),
                        p.getAbsenceDate(),
                        p.getStatus(),
                        p.getReason()
                ))
                .collect(Collectors.toList());
    }

}
