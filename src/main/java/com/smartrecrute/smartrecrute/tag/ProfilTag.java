package com.smartrecrute.smartrecrute.tag;

import com.smartrecrute.smartrecrute.enums.NiveauExpertise;
import com.smartrecrute.smartrecrute.utilisateur.Candidat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "profil_tag")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}