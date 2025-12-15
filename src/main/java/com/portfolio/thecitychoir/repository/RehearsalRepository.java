package com.portfolio.thecitychoir.repository;

import com.portfolio.thecitychoir.entity.RehearsalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface RehearsalRepository extends JpaRepository<RehearsalEntity, Long> {
    boolean existsByRehearsalDate(LocalDate rehearsalDate);

}
