package com.smartrecrute.smartrecrute.utilisateur;

import com.smartrecrute.smartrecrute.enums.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "administrateur")
public class Administrateur extends Utilisateur {

    public Administrateur() {
        super();
    }

    public Administrateur(String nom, String email, String motDePasse) {
        super(nom, email, motDePasse, Role.ADMIN);
    }
}