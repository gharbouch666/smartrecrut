package com.smartrecrute.smartrecrute.utilisateur;

import com.smartrecrute.smartrecrute.enums.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "candidat")
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

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getCvUrl() { return cvUrl; }
    public void setCvUrl(String cvUrl) { this.cvUrl = cvUrl; }
}