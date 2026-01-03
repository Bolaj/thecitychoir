package com.portfolio.thecitychoir.repository;

import com.portfolio.thecitychoir.entity.ProfileEntity;
import com.portfolio.thecitychoir.entity.RequestPermissionEntity;
import com.portfolio.thecitychoir.permission.PermissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RequestPermissionRepository extends JpaRepository<RequestPermissionEntity, Long> {
    List<RequestPermissionEntity> findByStatus(PermissionStatus status);
    List<RequestPermissionEntity> findByProfile(ProfileEntity profile);
    boolean existsByProfileAndAbsenceDate(ProfileEntity profile, LocalDate date);
}
