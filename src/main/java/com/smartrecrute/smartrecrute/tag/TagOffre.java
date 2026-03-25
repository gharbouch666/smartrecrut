package com.smartrecrute.smartrecrute.tag;

import com.smartrecrute.smartrecrute.offre.Offre;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tag_offre")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}