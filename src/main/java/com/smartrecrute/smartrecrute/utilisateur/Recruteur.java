package com.smartrecrute.smartrecrute.utilisateur;

import com.smartrecrute.smartrecrute.enums.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "recruteur")
public class Recruteur extends Utilisateur {

    private String poste;

    public Recruteur() {
        super();
    }

    public Recruteur(String nom, String email, String motDePasse, String poste) {
        super(nom, email, motDePasse, Role.RECRUTEUR);
        this.poste = poste;
    }

    public String getPoste() { return poste; }
    public void setPoste(String poste) { this.poste = poste; }
}