package com.smartrecrute.smartrecrute.tag;

import com.smartrecrute.smartrecrute.enums.NiveauExpertise;
import com.smartrecrute.smartrecrute.utilisateur.Candidat;
import jakarta.persistence.*;

@Entity
@Table(name = "profil_tag")
public class ProfilTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NiveauExpertise niveau;

    @ManyToOne
    @JoinColumn(name = "candidat_id")
    private Candidat candidat;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    public ProfilTag() {}

    public ProfilTag(NiveauExpertise niveau, Candidat candidat, Tag tag) {
        this.niveau = niveau;
        this.candidat = candidat;
        this.tag = tag;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public NiveauExpertise getNiveau() { return niveau; }
    public void setNiveau(NiveauExpertise niveau) { this.niveau = niveau; }

    public Candidat getCandidat() { return candidat; }
    public void setCandidat(Candidat candidat) { this.candidat = candidat; }

    public Tag getTag() { return tag; }
    public void setTag(Tag tag) { this.tag = tag; }
}