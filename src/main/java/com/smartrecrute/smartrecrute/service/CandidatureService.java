package com.smartrecrute.smartrecrute.service;

import com.smartrecrute.smartrecrute.entity.Candidature;
import java.util.List;
import java.util.Map;

public interface CandidatureService {
    List<Candidature> getAll();
    Candidature getById(Long id);
    Candidature create(Candidature candidature);
    Candidature applyToOffre(Long candidatId, Long offreId);
    Candidature update(Long id, Candidature candidature);
    void delete(Long id);
    List<Candidature> getByOffre(Long offreId);
    List<Candidature> getRankedByOffre(Long offreId);
    List<Candidature> getByCandidat(Long candidatId);
    List<Candidature> getByRecruteur(Long recruteurId);
    Candidature updateStatut(Long id, String statut);
    void recalculateAllForCandidat(Long candidatId);
    Map<String, Object> getCandidatureWithSkills(Long id);
}