package com.smartrecrute.smartrecrute.offre;

import com.smartrecrute.smartrecrute.enums.StatutOffre;
import com.smartrecrute.smartrecrute.enums.TypeContrat;
import com.smartrecrute.smartrecrute.enums.NiveauExperience;
import com.smartrecrute.smartrecrute.utilisateur.Recruteur;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "offre")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Offre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    private String description;

    @Enumerated(EnumType.STRING)
    private TypeContrat typeContrat;

    private String departement;
    private String localisation;

    @Enumerated(EnumType.STRING)
    private NiveauExperience experienceRequise;

    private String avantages;
    private Double scoreMinimum;
    private Integer nbCandidatsMax;

    private LocalDateTime dateCloture;
    private LocalDateTime datePublication;

    @Enumerated(EnumType.STRING)
    private StatutOffre statut;

    @ManyToOne
    @JoinColumn(name = "recruteur_id")
    private Recruteur recruteur;
}