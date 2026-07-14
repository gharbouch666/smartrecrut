package com.smartrecrute.smartrecrute.service;

import com.smartrecrute.smartrecrute.repository.OffreRepository;
import com.smartrecrute.smartrecrute.repository.TagOffreRepository;
import com.smartrecrute.smartrecrute.repository.TagRepository;
import com.smartrecrute.smartrecrute.repository.RecruteurRepository;
import com.smartrecrute.smartrecrute.repository.CandidatureRepository;
import com.smartrecrute.smartrecrute.entity.Offre;
import com.smartrecrute.smartrecrute.entity.TagOffre;
import com.smartrecrute.smartrecrute.entity.Tag;
import com.smartrecrute.smartrecrute.entity.Recruteur;
import com.smartrecrute.smartrecrute.dto.TagOffreRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;

@Service
public class OffreServiceImpl implements OffreService {

    @Autowired
    private OffreRepository repository;

    @Autowired
    private TagOffreRepository tagOffreRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private RecruteurRepository recruteurRepository;

    @Autowired
    private CandidatureRepository candidatureRepository;

    @Override
    public List<Offre> getAll() {
        return repository.findAll();
    }

    @Override
    public List<Offre> getOpenJobs() {
        return repository.findByStatutOuvert();
    }

    @Override
    public Offre getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offre not found with id: " + id));
    }

    @Override
    public Offre create(Offre offre) {
        // Simple logic: just save. If recruiter already set in request, use it. Otherwise don't try to auto-assign.
        return repository.save(offre);
    }

    @Override
    public Offre update(Long id, Offre offreDetails) {
        Offre offre = getById(id);
        offre.setTitre(offreDetails.getTitre());
        offre.setDescription(offreDetails.getDescription());
        offre.setTypeContrat(offreDetails.getTypeContrat());
        offre.setDepartement(offreDetails.getDepartement());
        offre.setLocalisation(offreDetails.getLocalisation());
        offre.setExperienceRequise(offreDetails.getExperienceRequise());
        offre.setAvantages(offreDetails.getAvantages());
        offre.setScoreMinimum(offreDetails.getScoreMinimum());
        offre.setNbCandidatsMax(offreDetails.getNbCandidatsMax());
        offre.setDateCloture(offreDetails.getDateCloture());
        offre.setStatut(offreDetails.getStatut());
        
        // Only update recruiter if explicitly provided and has valid ID
        if (offreDetails.getRecruteur() != null && offreDetails.getRecruteur().getId() != null) {
            Long rid = offreDetails.getRecruteur().getId();
            Recruteur recruiter = recruteurRepository.findById(rid).orElse(null);
            offre.setRecruteur(recruiter);
        }
        
        return repository.save(offre);
    }

    @Override
    public void delete(Long id) {
        // First delete ALL related Candidatures (these reference this offre)
        List<com.smartrecrute.smartrecrute.entity.Candidature> candidatures = candidatureRepository.findByOffreId(id);
        if (!candidatures.isEmpty()) {
            candidatureRepository.deleteAll(candidatures);
        }
        // Then delete related TagOffre records
        tagOffreRepository.deleteByOffreId(id);
        // Then delete the offre
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public Offre createWithTags(Offre offre, List<TagOffreRequest> tags) {
        // First check if recruiter was explicitly provided
        if (offre.getRecruteur() == null || offre.getRecruteur().getId() == null) {
            try {
                var auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated()) {
                    String email = auth.getName();
                    if (email != null && !email.isEmpty()) {
                        Recruteur recruiter = recruteurRepository.findByEmail(email);
                        if (recruiter != null) {
                            offre.setRecruteur(recruiter);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("OffreServiceImpl.createWithTags: Could not get recruiter from auth: " + e.getMessage());
            }
        }
        Offre savedOffre = repository.save(offre);
        for (TagOffreRequest req : tags) {
            Tag tag = tagRepository.findById(req.getTagId())
                    .orElseThrow(() -> new RuntimeException("Tag not found with id: " + req.getTagId()));
            TagOffre tagOffre = new TagOffre();
            tagOffre.setOffre(savedOffre);
            tagOffre.setTag(tag);
            tagOffre.setObligatoire(req.getObligatoire());
            tagOffre.setPoids(req.getPoids() != null ? req.getPoids() : 1.0);
            tagOffreRepository.save(tagOffre);
        }
        return savedOffre;
    }

    @Override
    @Transactional
    public Offre updateWithTags(Long id, Offre offreDetails, List<TagOffreRequest> tags) {
        Offre offre = getById(id);
        offre.setTitre(offreDetails.getTitre());
        offre.setDescription(offreDetails.getDescription());
        offre.setTypeContrat(offreDetails.getTypeContrat());
        offre.setDepartement(offreDetails.getDepartement());
        offre.setLocalisation(offreDetails.getLocalisation());
        offre.setExperienceRequise(offreDetails.getExperienceRequise());
        offre.setAvantages(offreDetails.getAvantages());
        offre.setScoreMinimum(offreDetails.getScoreMinimum());
        offre.setNbCandidatsMax(offreDetails.getNbCandidatsMax());
        offre.setDateCloture(offreDetails.getDateCloture());
        offre.setStatut(offreDetails.getStatut());
        
        // Only update recruiter if explicitly provided and has valid ID
        if (offreDetails.getRecruteur() != null && offreDetails.getRecruteur().getId() != null) {
            Long rid = offreDetails.getRecruteur().getId();
            Recruteur recruiter = recruteurRepository.findById(rid).orElse(null);
            offre.setRecruteur(recruiter);
        }
        
        tagOffreRepository.deleteByOffreId(id);
        for (TagOffreRequest req : tags) {
            Tag tag = tagRepository.findById(req.getTagId())
                    .orElseThrow(() -> new RuntimeException("Tag not found with id: " + req.getTagId()));
            TagOffre tagOffre = new TagOffre();
            tagOffre.setOffre(offre);
            tagOffre.setTag(tag);
            tagOffre.setObligatoire(req.getObligatoire());
            tagOffre.setPoids(req.getPoids() != null ? req.getPoids() : 1.0);
            tagOffreRepository.save(tagOffre);
        }
        return repository.save(offre);
    }

    @Override
    public List<TagOffre> getTagsByOffre(Long offreId) {
        return tagOffreRepository.findByOffreId(offreId);
    }
}