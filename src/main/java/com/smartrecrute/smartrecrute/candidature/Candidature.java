package com.smartrecrute.smartrecrute.candidature;

import com.smartrecrute.smartrecrute.enums.StatutKanban;
import com.smartrecrute.smartrecrute.offre.Offre;
import com.smartrecrute.smartrecrute.utilisateur.Candidat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidature")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}