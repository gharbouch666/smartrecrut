package com.smartrecrute.smartrecrute.candidature;

import com.smartrecrute.smartrecrute.enums.StatutKanban;
import com.smartrecrute.smartrecrute.offre.Offre;
import com.smartrecrute.smartrecrute.utilisateur.Candidat;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidature")
public class Candidature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime datePostulation;
    private Double scoreTotal;

    @Enumerated(EnumType.STRING)
    private StatutKanban statut;

    @ManyToOne
    @JoinColumn(name = "candidat_id")
    private Candidat candidat;

    @ManyToOne
    @JoinColumn(name = "offre_id")
    private Offre offre;

    @OneToOne(mappedBy = "candidature", cascade = CascadeType.ALL)
    private ScoreMatching scoreMatching;

    public Candidature() {}

    public Candidature(LocalDateTime datePostulation, Double scoreTotal, StatutKanban statut,
                      Candidat candidat, Offre offre) {
        this.datePostulation = datePostulation;
        this.scoreTotal = scoreTotal;
        this.statut = statut;
        this.candidat = candidat;
        this.offre = offre;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getDatePostulation() { return datePostulation; }
    public void setDatePostulation(LocalDateTime datePostulation) { this.datePostulation = datePostulation; }

    public Double getScoreTotal() { return scoreTotal; }
    public void setScoreTotal(Double scoreTotal) { this.scoreTotal = scoreTotal; }

    public StatutKanban getStatut() { return statut; }
    public void setStatut(StatutKanban statut) { this.statut = statut; }

    public Candidat getCandidat() { return candidat; }
    public void setCandidat(Candidat candidat) { this.candidat = candidat; }

    public Offre getOffre() { return offre; }
    public void setOffre(Offre offre) { this.offre = offre; }

    public ScoreMatching getScoreMatching() { return scoreMatching; }
    public void setScoreMatching(ScoreMatching scoreMatching) { this.scoreMatching = scoreMatching; }
}