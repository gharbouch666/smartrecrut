package com.smartrecrute.smartrecrute.tag;

import com.smartrecrute.smartrecrute.offre.Offre;
import jakarta.persistence.*;

@Entity
@Table(name = "tag_offre")
public class TagOffre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean obligatoire;
    private Double poids;

    @ManyToOne
    @JoinColumn(name = "offre_id")
    private Offre offre;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    public TagOffre() {}

    public TagOffre(Boolean obligatoire, Double poids, Offre offre, Tag tag) {
        this.obligatoire = obligatoire;
        this.poids = poids;
        this.offre = offre;
        this.tag = tag;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Boolean getObligatoire() { return obligatoire; }
    public void setObligatoire(Boolean obligatoire) { this.obligatoire = obligatoire; }

    public Double getPoids() { return poids; }
    public void setPoids(Double poids) { this.poids = poids; }

    public Offre getOffre() { return offre; }
    public void setOffre(Offre offre) { this.offre = offre; }

    public Tag getTag() { return tag; }
    public void setTag(Tag tag) { this.tag = tag; }
}