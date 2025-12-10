package com.portfolio.thecitychoir.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_attendance", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"profile_id", "rehearsal_id"})
})
@Getter
@Setter
@NoArgsConstructor
public class AttendanceEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private ProfileEntity profile;

    @ManyToOne(optional = false)
    private RehearsalEntity rehearsal;

    private LocalDateTime checkedAt;
    private Double lat;
    private Double lng;
    private String deviceId;
    private String qrToken;
    private String note;
}
