package com.portfolio.thecitychoir.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_rehearsals")
@Getter
@Setter
@NoArgsConstructor
public class RehearsalEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double lat;
    private Double lng;

    private Integer radiusMeters;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String currentQrToken;
    private LocalDateTime currentQrExpiresAt;
}

