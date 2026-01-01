package com.portfolio.thecitychoir.repository;

import com.portfolio.thecitychoir.entity.DeviceBindingEntity;
import com.portfolio.thecitychoir.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface DeviceBindRepository extends JpaRepository<DeviceBindingEntity, Long> {
    Optional<DeviceBindingEntity> findByProfileAndDeviceId(ProfileEntity profile, String deviceId);
}
