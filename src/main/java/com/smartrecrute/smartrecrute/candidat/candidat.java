package com.smartrecrute.smartrecrute.candidat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "candidat")
public class candidat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nom;
	private String prenom;
	private String email;
	private String telephone;
	private String cv;
	private String status;

	public candidat() {
	}

	public candidat(String nom, String prenom, String email, String telephone, String cv, String status) {
		this.nom = nom;
		this.prenom = prenom;
		this.email = email;
		this.telephone = telephone;
		this.cv = cv;
		this.status = status;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getNom() { return nom; }
	public void setNom(String nom) { this.nom = nom; }
	public String getPrenom() { return prenom; }
	public void setPrenom(String prenom) { this.prenom = prenom; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getTelephone() { return telephone; }
	public void setTelephone(String telephone) { this.telephone = telephone; }
	public String getCv() { return cv; }
	public void setCv(String cv) { this.cv = cv; }
	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
}
