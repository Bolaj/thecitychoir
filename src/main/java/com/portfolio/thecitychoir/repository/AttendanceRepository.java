package com.portfolio.thecitychoir.repository;

import com.portfolio.thecitychoir.entity.AttendanceEntity;
import com.portfolio.thecitychoir.entity.ProfileEntity;
import com.portfolio.thecitychoir.entity.RehearsalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Long> {
    boolean existsByProfileAndRehearsal(ProfileEntity profile, RehearsalEntity rehearsal);
    List<AttendanceEntity> findByRehearsal (RehearsalEntity rehearsal);
    long countByRehearsal (RehearsalEntity rehearsal);

    interface AttendancePartCountProjection {
        String getPart();
        Long getCnt();
    }


    @Query("""
    SELECT p.part AS part, COUNT(a.id) AS cnt
    FROM AttendanceEntity a
    JOIN a.profile p
    WHERE a.rehearsal.id = :rehearsalId
    GROUP BY p.part
    """)
    List<AttendancePartCountProjection> countByPart(
            @Param("rehearsalId") Long rehearsalId
    );
}