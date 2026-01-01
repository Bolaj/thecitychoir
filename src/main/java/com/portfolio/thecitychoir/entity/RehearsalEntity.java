package com.portfolio.thecitychoir.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalDate;



@Entity
@Table(
        name = "tbl_rehearsals",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"rehearsal_date"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RehearsalEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double lat;
    private Double lng;

    private Integer radiusMeters;


    private LocalDateTime startTime;

    private LocalDateTime endTime;
    private LocalDateTime createdAt;

    @Column(nullable = false, unique = true)
    private LocalDate rehearsalDate;

    private String currentQrToken;
    private LocalDateTime currentQrExpiresAt;
}

