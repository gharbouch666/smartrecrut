package com.smartrecrute.smartrecrute.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "candidat")
@NoArgsConstructor
public class Candidat extends Utilisateur {

	private LocalDate dateNaissance;
	private String telephone;
	private String cvUrl;
	private String lettreMotivationUrl;
	private String niveauScolaire;
	private String experience;
	private String permisDeConduire;
	private String ville;
	private String linkedin;

	public LocalDate getDateNaissance() { return dateNaissance; }
	public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
	public String getTelephone() { return telephone; }
	public void setTelephone(String telephone) { this.telephone = telephone; }
	public String getCvUrl() { return cvUrl; }
	public void setCvUrl(String cvUrl) { this.cvUrl = cvUrl; }
	public String getLettreMotivationUrl() { return lettreMotivationUrl; }
	public void setLettreMotivationUrl(String lettreMotivationUrl) { this.lettreMotivationUrl = lettreMotivationUrl; }
	public String getNiveauScolaire() { return niveauScolaire; }
	public void setNiveauScolaire(String niveauScolaire) { this.niveauScolaire = niveauScolaire; }
	public String getExperience() { return experience; }
	public void setExperience(String experience) { this.experience = experience; }
	public String getPermisDeConduire() { return permisDeConduire; }
	public void setPermisDeConduire(String permisDeConduire) { this.permisDeConduire = permisDeConduire; }
	public String getVille() { return ville; }
	public void setVille(String ville) { this.ville = ville; }
	public String getLinkedin() { return linkedin; }
	public void setLinkedin(String linkedin) { this.linkedin = linkedin; }
}
