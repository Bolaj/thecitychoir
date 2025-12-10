package com.portfolio.thecitychoir.repository;

import com.portfolio.thecitychoir.entity.AttendanceEntity;
import com.portfolio.thecitychoir.entity.ProfileEntity;
import com.portfolio.thecitychoir.entity.RehearsalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Long> {
    boolean existsByProfileAndRehearsal(ProfileEntity profile, RehearsalEntity rehearsal);
}