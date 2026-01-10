package com.portfolio.thecitychoir.entity;

import com.portfolio.thecitychoir.permission.PermissionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_request_permissions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestPermissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private ProfileEntity profile;

    private String registrationNumber;

    @Column(nullable = false, length = 500)
    private String reason;

    @Column(nullable = false)
    private LocalDate absenceDate;

    @Enumerated(EnumType.STRING)
    private PermissionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decided_by")
    private ProfileEntity decidedBy;

    private LocalDateTime decidedAt;

    private LocalDateTime createdAt;
}
