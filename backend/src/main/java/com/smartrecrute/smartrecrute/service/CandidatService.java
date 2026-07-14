package com.smartrecrute.smartrecrute.service;

import com.smartrecrute.smartrecrute.entity.Candidat;
import com.smartrecrute.smartrecrute.entity.ProfilTag;
import com.smartrecrute.smartrecrute.dto.ProfilTagRequest;
import java.util.List;

public interface CandidatService {
    List<Candidat> getAll();
    Candidat getById(Long id);
    Candidat create(Candidat candidat);
    Candidat update(Long id, Candidat candidat);
    void updateCvUrl(Long id, String cvUrl);
    void updateLettreMotivationUrl(Long id, String lettreUrl);
    void delete(Long id);
    Candidat findByEmail(String email);
    List<ProfilTag> getSkillsByCandidat(Long candidatId);
    ProfilTag addSkill(Long candidatId, ProfilTagRequest request);
    ProfilTag updateSkill(Long candidatId, Long tagId, ProfilTagRequest request);
    void removeSkill(Long candidatId, Long tagId);
    Candidat updateCompetences(Long candidatId, List<ProfilTagRequest> competences);
    List<Candidat> searchCandidates(String query);
}