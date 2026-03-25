package com.smartrecrute.smartrecrute.utilisateur;

import com.smartrecrute.smartrecrute.enums.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "recruteur")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Recruteur extends Utilisateur {

    private String poste;

    public Recruteur() {
        super();
    }

    public Recruteur(String nom, String email, String motDePasse, String poste) {
        super(nom, email, motDePasse, Role.RECRUTEUR);
        this.poste = poste;
    }
}