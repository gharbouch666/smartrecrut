package com.smartrecrute.smartrecrute.service;

import com.smartrecrute.smartrecrute.repository.CandidatRepository;
import com.smartrecrute.smartrecrute.repository.ProfilTagRepository;
import com.smartrecrute.smartrecrute.repository.TagRepository;
import com.smartrecrute.smartrecrute.entity.Candidat;
import com.smartrecrute.smartrecrute.entity.ProfilTag;
import com.smartrecrute.smartrecrute.entity.Tag;
import com.smartrecrute.smartrecrute.dto.ProfilTagRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class CandidatServiceImpl implements CandidatService {

    @Autowired
    private CandidatRepository repository;

    @Autowired
    private ProfilTagRepository profilTagRepository;

    @Autowired
    private TagRepository tagRepository;

    @Override
    public List<Candidat> getAll() {
        return repository.findAll();
    }

    @Override
    public Candidat getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidat not found with id: " + id));
    }

    @Override
    public Candidat create(Candidat candidat) {
        return repository.save(candidat);
    }

    @Override
    public Candidat update(Long id, Candidat candidatDetails) {
        Candidat candidat = getById(id);
        // Update all fields
        if (candidatDetails.getNom() != null) {
            candidat.setNom(candidatDetails.getNom());
        }
        if (candidatDetails.getTelephone() != null) {
            candidat.setTelephone(candidatDetails.getTelephone());
        }
        if (candidatDetails.getVille() != null) {
            candidat.setVille(candidatDetails.getVille());
        }
        if (candidatDetails.getDateNaissance() != null) {
            candidat.setDateNaissance(candidatDetails.getDateNaissance());
        }
        if (candidatDetails.getNiveauScolaire() != null) {
            candidat.setNiveauScolaire(candidatDetails.getNiveauScolaire());
        }
        if (candidatDetails.getExperience() != null) {
            candidat.setExperience(candidatDetails.getExperience());
        }
        if (candidatDetails.getPermisDeConduire() != null) {
            candidat.setPermisDeConduire(candidatDetails.getPermisDeConduire());
        }
        if (candidatDetails.getLinkedin() != null) {
            candidat.setLinkedin(candidatDetails.getLinkedin());
        }
        if (candidatDetails.getMotDePasse() != null && !candidatDetails.getMotDePasse().isEmpty()) {
            candidat.setMotDePasse(candidatDetails.getMotDePasse());
        }
        return repository.save(candidat);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
    
    @Override
    public void updateCvUrl(Long id, String cvUrl) {
        Candidat candidat = getById(id);
        candidat.setCvUrl(cvUrl);
        repository.save(candidat);
    }
    
    @Override
    public void updateLettreMotivationUrl(Long id, String lettreUrl) {
        Candidat candidat = getById(id);
        candidat.setLettreMotivationUrl(lettreUrl);
        repository.save(candidat);
    }

    @Override
    public Candidat findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public List<ProfilTag> getSkillsByCandidat(Long candidatId) {
        return profilTagRepository.findByCandidatId(candidatId);
    }

    @Override
    public ProfilTag addSkill(Long candidatId, ProfilTagRequest request) {
        Candidat candidat = getById(candidatId);
        Tag tag = tagRepository.findById(request.getTagId())
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + request.getTagId()));

        return profilTagRepository.findByCandidatIdAndTagId(candidatId, request.getTagId())
                .map(existingSkill -> {
                    existingSkill.setNiveau(request.getNiveau());
                    return profilTagRepository.save(existingSkill);
                })
                .orElseGet(() -> {
                    ProfilTag profilTag = new ProfilTag();
                    profilTag.setCandidat(candidat);
                    profilTag.setTag(tag);
                    profilTag.setNiveau(request.getNiveau());
                    return profilTagRepository.save(profilTag);
                });
    }

    @Override
    public ProfilTag updateSkill(Long candidatId, Long tagId, ProfilTagRequest request) {
       ProfilTag profilTag = profilTagRepository.findByCandidatIdAndTagId(candidatId, tagId)
                .orElseThrow(() -> new RuntimeException("Skill not found for this candidat"));
        profilTag.setNiveau(request.getNiveau());
        return profilTagRepository.save(profilTag);
    }

    @Override
    public void removeSkill(Long candidatId, Long tagId) {
        ProfilTag profilTag = profilTagRepository.findByCandidatIdAndTagId(candidatId, tagId)
                .orElseThrow(() -> new RuntimeException("Skill not found for this candidat"));
        profilTagRepository.delete(profilTag);
    }

    @Override
    @Transactional
    public Candidat updateCompetences(Long candidatId, List<ProfilTagRequest> competences) {
        Candidat candidat = getById(candidatId);
        profilTagRepository.deleteByCandidatId(candidatId);
        
        List<ProfilTag> newSkills = new ArrayList<>();
        for (ProfilTagRequest req : competences) {
            Tag tag = tagRepository.findById(req.getTagId())
                    .orElseThrow(() -> new RuntimeException("Tag not found with id: " + req.getTagId()));
            
            final Long tagId = req.getTagId();
            boolean exists = newSkills.stream()
                    .anyMatch(pt -> pt.getTag().getId().equals(tagId));
            
            if (exists) {
                newSkills.removeIf(pt -> pt.getTag().getId().equals(tagId));
            }
            
            ProfilTag pt = new ProfilTag();
            pt.setCandidat(candidat);
            pt.setTag(tag);
            pt.setNiveau(req.getNiveau());
            newSkills.add(profilTagRepository.save(pt));
        }
        return candidat;
    }

    @Override
    public List<Candidat> searchCandidates(String query) {
        if (query == null || query.trim().isEmpty()) {
            return repository.findAll();
        }
        
        String lowerQuery = query.toLowerCase();
        List<Candidat> allCandidats = repository.findAll();
        List<Candidat> results = new ArrayList<>();
        
        for (Candidat c : allCandidats) {
            boolean match = false;
            
            // Search by name
            if (c.getNom() != null && c.getNom().toLowerCase().contains(lowerQuery)) {
                match = true;
            }
            
            // Search by email
            if (!match && c.getEmail() != null && c.getEmail().toLowerCase().contains(lowerQuery)) {
                match = true;
            }
            
            // Search by skills
            if (!match) {
                List<ProfilTag> skills = profilTagRepository.findByCandidatId(c.getId());
                for (ProfilTag skill : skills) {
                    String tagName = skill.getTag().getLibelle().toLowerCase();
                    if (tagName.contains(lowerQuery)) {
                        match = true;
                        break;
                    }
                }
            }
            
            if (match) {
                results.add(c);
            }
        }
        
        return results;
    }
}