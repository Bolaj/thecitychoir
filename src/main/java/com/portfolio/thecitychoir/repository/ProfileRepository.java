package com.portfolio.thecitychoir.repository;

import com.portfolio.thecitychoir.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {
    Optional<ProfileEntity> findByEmail(String email);
    Optional<ProfileEntity> findByActivationToken(String activationToken);
    boolean existsByEmail(String email);
    Optional<ProfileEntity> findByRegistrationNumber(String registrationNumber);
    Iterable<ProfileEntity> findByPart(String part);
    long countByPart(String part);
    List<MemberEmailProjection> findAllByIsActiveTrue();

    interface MemberEmailProjection {
        String getEmail();
        String getFullName();
    }






}
