package com.smartrecrute.smartrecrute.candidature;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "score_matching")
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

    public ScoreMatching() {}

    public ScoreMatching(Double scoreTotal, Double scoreObligatoires, Double scoreBonus,
                        LocalDateTime dateCalcul, Candidature candidature) {
        this.scoreTotal = scoreTotal;
        this.scoreObligatoires = scoreObligatoires;
        this.scoreBonus = scoreBonus;
        this.dateCalcul = dateCalcul;
        this.candidature = candidature;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getScoreTotal() { return scoreTotal; }
    public void setScoreTotal(Double scoreTotal) { this.scoreTotal = scoreTotal; }

    public Double getScoreObligatoires() { return scoreObligatoires; }
    public void setScoreObligatoires(Double scoreObligatoires) { this.scoreObligatoires = scoreObligatoires; }

    public Double getScoreBonus() { return scoreBonus; }
    public void setScoreBonus(Double scoreBonus) { this.scoreBonus = scoreBonus; }

    public LocalDateTime getDateCalcul() { return dateCalcul; }
    public void setDateCalcul(LocalDateTime dateCalcul) { this.dateCalcul = dateCalcul; }

    public Candidature getCandidature() { return candidature; }
    public void setCandidature(Candidature candidature) { this.candidature = candidature; }
}