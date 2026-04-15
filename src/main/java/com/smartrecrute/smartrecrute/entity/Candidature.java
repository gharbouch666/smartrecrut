package com.smartrecrute.smartrecrute.entity;

import com.smartrecrute.smartrecrute.enums.StatutKanban;
import com.smartrecrute.smartrecrute.entity.Candidat;
import com.smartrecrute.smartrecrute.entity.Offre;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidature")
public class Candidature {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDateTime datePostulation;
	private Double scoreTotal;

	@Enumerated(EnumType.STRING)
	private StatutKanban statut;

	@ManyToOne
	@JoinColumn(name = "candidat_id")
	private Candidat candidat;

	@ManyToOne
	@JoinColumn(name = "offre_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Offre offre;

	@OneToOne(mappedBy = "candidature", cascade = CascadeType.ALL)
	private ScoreMatching scoreMatching;

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public LocalDateTime getDatePostulation() { return datePostulation; }
	public void setDatePostulation(LocalDateTime datePostulation) { this.datePostulation = datePostulation; }

	public Double getScoreTotal() { return scoreTotal; }
	public void setScoreTotal(Double scoreTotal) { this.scoreTotal = scoreTotal; }

	public StatutKanban getStatut() { return statut; }
	public void setStatut(StatutKanban statut) { this.statut = statut; }

	public Candidat getCandidat() { return candidat; }
	public void setCandidat(Candidat candidat) { this.candidat = candidat; }

	public Offre getOffre() { return offre; }
	public void setOffre(Offre offre) { this.offre = offre; }

	public ScoreMatching getScoreMatching() { return scoreMatching; }
	public void setScoreMatching(ScoreMatching scoreMatching) { this.scoreMatching = scoreMatching; }
}
