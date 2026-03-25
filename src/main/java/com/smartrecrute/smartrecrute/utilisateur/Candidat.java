package com.smartrecrute.smartrecrute.utilisateur;

import com.smartrecrute.smartrecrute.enums.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "candidat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Candidat extends Utilisateur {

    private LocalDate dateNaissance;
    private String telephone;
    private String cvUrl;

    public Candidat() {
        super();
    }

    public Candidat(String nom, String email, String motDePasse, LocalDate dateNaissance, String telephone, String cvUrl) {
        super(nom, email, motDePasse, Role.CANDIDAT);
        this.dateNaissance = dateNaissance;
        this.telephone = telephone;
        this.cvUrl = cvUrl;
    }
}