package com.smartrecrute.smartrecrute.service;

import com.smartrecrute.smartrecrute.repository.CandidatureRepository;
import com.smartrecrute.smartrecrute.repository.CandidatRepository;
import com.smartrecrute.smartrecrute.repository.OffreRepository;
import com.smartrecrute.smartrecrute.entity.Candidature;
import com.smartrecrute.smartrecrute.entity.Candidat;
import com.smartrecrute.smartrecrute.entity.Offre;
import com.smartrecrute.smartrecrute.enums.StatutKanban;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smartrecrute.smartrecrute.entity.ProfilTag;
import com.smartrecrute.smartrecrute.entity.Tag;
import com.smartrecrute.smartrecrute.repository.ProfilTagRepository;
import com.smartrecrute.smartrecrute.repository.TagRepository;
import com.smartrecrute.smartrecrute.entity.TagOffre;
import com.smartrecrute.smartrecrute.repository.TagOffreRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CandidatureServiceImpl implements CandidatureService {

    @Autowired
    private CandidatureRepository repository;

    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private OffreRepository offreRepository;

    @Autowired
    private MatchingService matchingService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ProfilTagRepository profilTagRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagOffreRepository tagOffreRepository;

    @Override
    public List<Candidature> getAll() {
        List<Candidature> list = repository.findAll();
        for (Candidature c : list) {
            if (c.getCandidat() != null && c.getOffre() != null) {
                double newScore = matchingService.calculateScore(c.getCandidat(), c.getOffre());
                c.setScoreTotal(newScore);
            }
        }
        return list;
    }

    @Override
    public Candidature getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidature not found with id: " + id));
    }

    @Override
    public Candidature create(Candidature candidature) {
        return repository.save(candidature);
    }

    @Override
    public Candidature applyToOffre(Long candidatId, Long offreId) {
        Candidat candidat = candidatRepository.findById(candidatId)
                .orElseThrow(() -> new RuntimeException("Candidat not found"));
        Offre offre = offreRepository.findById(offreId)
                .orElseThrow(() -> new RuntimeException("Offre not found"));

        Candidature candidature = new Candidature();
        candidature.setCandidat(candidat);
        candidature.setOffre(offre);
        candidature.setDatePostulation(LocalDateTime.now());
        candidature.setStatut(StatutKanban.A_TRIER);

        double score = matchingService.calculateScore(candidat, offre);
        candidature.setScoreTotal(score);

        Candidature saved = repository.save(candidature);

        Double minimumScore = offre.getScoreMinimum();
        if (minimumScore != null && score < minimumScore) {
            emailService.sendRejectionEmail(candidat, offre, score);
        } else {
            emailService.sendApplicationReceived(candidat, offre);
        }

        return saved;
    }

    @Override
    public Candidature update(Long id, Candidature candidatureDetails) {
        Candidature candidature = getById(id);
        candidature.setScoreTotal(candidatureDetails.getScoreTotal());
        candidature.setStatut(candidatureDetails.getStatut());
        return repository.save(candidature);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Candidature> getByOffre(Long offreId) {
        Offre offre = offreRepository.findById(offreId)
                .orElseThrow(() -> new RuntimeException("Offre not found"));
        return repository.findByOffre(offre);
    }

    @Override
    public List<Candidature> getRankedByOffre(Long offreId) {
        List<Candidature> list = getByOffre(offreId);
        for (Candidature c : list) {
            double newScore = matchingService.calculateScore(c.getCandidat(), c.getOffre());
            c.setScoreTotal(newScore);
        }
        list.sort(Comparator.comparing(
            c -> c.getScoreTotal() != null ? c.getScoreTotal() : 0.0,
            Comparator.reverseOrder()
        ));
        return list;
    }

    @Override
    public List<Candidature> getByCandidat(Long candidatId) {
        List<Candidature> list = repository.findByCandidatId(candidatId);
        for (Candidature c : list) {
            if (c.getCandidat() != null && c.getOffre() != null) {
                double newScore = matchingService.calculateScore(c.getCandidat(), c.getOffre());
                c.setScoreTotal(newScore);
            }
        }
        return list;
    }

    @Override
    public List<Candidature> getByRecruteur(Long recruteurId) {
        List<Candidature> all = repository.findAll();
        List<Candidature> filtered = all.stream()
            .filter(c -> c.getOffre() != null && c.getOffre().getRecruteur() != null)
            .filter(c -> c.getOffre().getRecruteur().getId().equals(recruteurId))
            .collect(Collectors.toList());
        for (Candidature c : filtered) {
            if (c.getCandidat() != null && c.getOffre() != null) {
                double newScore = matchingService.calculateScore(c.getCandidat(), c.getOffre());
                c.setScoreTotal(newScore);
            }
        }
        return filtered;
    }

    @Override
    public Candidature updateStatut(Long id, String statut) {
        Candidature candidature = getById(id);
        try {
            candidature.setStatut(StatutKanban.valueOf(statut));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid statut: " + statut);
        }
        return repository.save(candidature);
    }

    @Override
    public void recalculateAllForCandidat(Long candidatId) {
        List<Candidature> list = repository.findByCandidatId(candidatId);
        for (Candidature c : list) {
            if (c.getOffre() != null) {
                double newScore = matchingService.calculateScore(c.getCandidat(), c.getOffre());
                c.setScoreTotal(newScore);
                repository.save(c);
            }
        }
    }

    @Override
    public Map<String, Object> getCandidatureWithSkills(Long id) {
        Candidature candidature = repository.findById(id).orElse(null);
        if (candidature == null) {
            return Map.of("error", "Candidature not found");
        }

        Map<String, Object> result = new HashMap<>();
        
        // Get candidate skills
        List<String> candidatSkills = new ArrayList<>();
        if (candidature.getCandidat() != null) {
            List<ProfilTag> profilTags = profilTagRepository.findByCandidatId(candidature.getCandidat().getId());
            for (ProfilTag pt : profilTags) {
                if (pt.getTag() != null && pt.getTag().getLibelle() != null) {
                    candidatSkills.add(pt.getTag().getLibelle());
                }
            }
        }
        
        // Get job tags
        List<String> jobTags = new ArrayList<>();
        if (candidature.getOffre() != null) {
            List<TagOffre> tagOffres = tagOffreRepository.findByOffreId(candidature.getOffre().getId());
            for (TagOffre to : tagOffres) {
                if (to.getTag() != null && to.getTag().getLibelle() != null) {
                    jobTags.add(to.getTag().getLibelle());
                }
            }
        }

        result.put("candidatSkills", candidatSkills);
        result.put("jobTags", jobTags);
        result.put("scoreTotal", candidature.getScoreTotal());
        result.put("jobTitle", candidature.getOffre() != null ? candidature.getOffre().getTitre() : "");
        result.put("candidatName", candidature.getCandidat() != null ? candidature.getCandidat().getNom() : "");

        return result;
    }
}