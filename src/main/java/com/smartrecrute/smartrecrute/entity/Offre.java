package com.smartrecrute.smartrecrute.entity;

import com.smartrecrute.smartrecrute.enums.StatutOffre;
import com.smartrecrute.smartrecrute.enums.TypeContrat;
import com.smartrecrute.smartrecrute.enums.NiveauExperience;
import com.smartrecrute.smartrecrute.entity.Recruteur;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "offre")
public class Offre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    
    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private TypeContrat typeContrat;

    private String departement;
    private String localisation;

    @Enumerated(EnumType.STRING)
    private NiveauExperience experienceRequise;

    @Column(columnDefinition = "TEXT")
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TypeContrat getTypeContrat() { return typeContrat; }
    public void setTypeContrat(TypeContrat typeContrat) { this.typeContrat = typeContrat; }

    public String getDepartement() { return departement; }
    public void setDepartement(String departement) { this.departement = departement; }

    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }

    public NiveauExperience getExperienceRequise() { return experienceRequise; }
    public void setExperienceRequise(NiveauExperience experienceRequise) { this.experienceRequise = experienceRequise; }

    public String getAvantages() { return avantages; }
    public void setAvantages(String avantages) { this.avantages = avantages; }

    public Double getScoreMinimum() { return scoreMinimum; }
    public void setScoreMinimum(Double scoreMinimum) { this.scoreMinimum = scoreMinimum; }

    public Integer getNbCandidatsMax() { return nbCandidatsMax; }
    public void setNbCandidatsMax(Integer nbCandidatsMax) { this.nbCandidatsMax = nbCandidatsMax; }

    public LocalDateTime getDateCloture() { return dateCloture; }
    public void setDateCloture(LocalDateTime dateCloture) { this.dateCloture = dateCloture; }

    public LocalDateTime getDatePublication() { return datePublication; }
    public void setDatePublication(LocalDateTime datePublication) { this.datePublication = datePublication; }

    public StatutOffre getStatut() { return statut; }
    public void setStatut(StatutOffre statut) { this.statut = statut; }

    public Recruteur getRecruteur() { return recruteur; }
    public void setRecruteur(Recruteur recruteur) { this.recruteur = recruteur; }
}