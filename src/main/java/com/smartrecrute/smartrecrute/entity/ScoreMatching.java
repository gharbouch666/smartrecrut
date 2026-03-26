package com.smartrecrute.smartrecrute.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "score_matching")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScoreMatching {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double scoreTotal;
    private Double scoreObligatoires;
    private Double scoreBonus;
    private LocalDateTime dateCalcul;

    @OneToOne
    @JoinColumn(name = "candidature_id")
    private Candidature candidature;
}