package com.portfolio.thecitychoir.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_device_bindings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"profile_id", "deviceId"})
})
@Getter
@Setter
@NoArgsConstructor
public class DeviceBindingEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private ProfileEntity profile;

    private String deviceId;
    private String deviceInfo;
    private boolean approved;
    private LocalDateTime boundAt;
}
