package com.smartrecrute.smartrecrute.rh;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rh")
public class rh {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nom;
	private String prenom;
	private String email;
	private String telephone;
	private String departement;

	public rh() {
	}

	public rh(String nom, String prenom, String email, String telephone, String departement) {
		this.nom = nom;
		this.prenom = prenom;
		this.email = email;
		this.telephone = telephone;
		this.departement = departement;
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
	public String getDepartement() { return departement; }
	public void setDepartement(String departement) { this.departement = departement; }
}
